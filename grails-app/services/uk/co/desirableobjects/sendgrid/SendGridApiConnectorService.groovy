package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import uk.co.desirableobjects.sendgrid.exception.SendGridCommunicationException
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class SendGridApiConnectorService {

    static transactional = false
    def grailsApplication

    class BodyPart {
        String name
        byte[] content

        void setContent(raw) {
            if (raw instanceof byte[]) {
                content = raw
            } else {
                content = raw.bytes
            }
        }

    }

    def post(SendGridEmail email) {

        RESTClient sendGrid = new RESTClient(grailsApplication.config.sendgrid?.api?.url ?: 'https://sendgrid.com/api/')
        Response response
        try {
            response = sendGrid.post(path: 'mail.send.json') {
                for (BodyPart part in prepareParameters(email)) {
                    multipart part.name, part.content
                }
            }
        } catch (RESTClientException rce) {
            if (rce.response) {
                return handle(rce.response)
            }
            throw new SendGridCommunicationException('Unknown error communicating with SendGrid', rce)
        }
        return handle(response)

    }

    private SendGridResponse handle(clientResponse) {

        return SendGridResponse.parse(clientResponse)

    }

    private List<BodyPart> prepareParameters(SendGridEmail email) {

        checkConfig(email)

        List<BodyPart> body = []
        if(!email.username) {
            body << new BodyPart(name: 'api_user', content: grailsApplication.config.sendgrid?.username.bytes)
        }

        if(!email.password) {
            body << new BodyPart(name: 'api_key', content: grailsApplication.config.sendgrid?.password.bytes)
        }

        email.toMap().each { String key, value ->
            if (value instanceof List<?>) {
                body.addAll(explode(key, value))
            } else {
                body << new BodyPart(name: key, content: value)
            }
        }

        return body

    }

    private void checkConfig(email) {
        if ((!grailsApplication.config.sendgrid.password || !grailsApplication.config.sendgrid.username) &&
            (!email.username || !email.password)) {
            throw new MissingCredentialsException()
        }
    }

    private List<BodyPart> explode(String key, List entries) {

        return entries.collect { entry ->
            new BodyPart(name: key, content: entry)
        }

    }

}

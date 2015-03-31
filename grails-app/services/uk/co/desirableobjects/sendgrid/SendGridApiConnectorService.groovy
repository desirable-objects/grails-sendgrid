package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.SendGridCommunicationException
import wslite.rest.*
import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import org.codehaus.groovy.grails.commons.GrailsApplication

class SendGridApiConnectorService {

    static transactional = false
    GrailsApplication grailsApplication

    class BodyPart {
        String name
        byte[] content

        void setContent(def raw) {
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
                prepareParameters(email).each { BodyPart part ->
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
    
    private SendGridResponse handle(def clientResponse) {

        return SendGridResponse.parse(clientResponse)

    }
    
    private List<BodyPart> prepareParameters(SendGridEmail email) {

        checkConfig(email)

        List<BodyPart> body = []
        if(!email.username)
            body << new BodyPart(name: 'api_user', content: grailsApplication.config.sendgrid?.username.bytes)

        if(!email.password)
            body << new BodyPart(name: 'api_key', content: grailsApplication.config.sendgrid?.password.bytes)

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

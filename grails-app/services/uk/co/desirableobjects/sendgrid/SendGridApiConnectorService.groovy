package uk.co.desirableobjects.sendgrid

import grails.config.Config
import grails.core.GrailsApplication
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import uk.co.desirableobjects.sendgrid.exception.SendGridCommunicationException
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

@CompileStatic
class SendGridApiConnectorService {

    static transactional = false

    GrailsApplication grailsApplication

    @CompileStatic
    static class BodyPart {
        String name
        byte[] content

        void setContent(raw) {
            if (raw instanceof byte[]) {
                content = (byte[]) raw
            } else {
                content = ((String) raw).bytes
            }
        }

    }

    @CompileDynamic
    SendGridResponse post(SendGridEmail email) {

        RESTClient sendGrid = new RESTClient(config.getProperty('sendgrid.api.url', String, 'https://sendgrid.com/api/'))
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
            body << new BodyPart(name: 'api_user', content: config.getProperty('sendgrid.username', String).bytes)
        }

        if(!email.password) {
            body << new BodyPart(name: 'api_key', content: config.getProperty('sendgrid.password', String).bytes)
        }

        email.toMap().each { String key, value ->
            if (value instanceof List) {
                body.addAll(explode(key, (List) value))
            } else {
                body << new BodyPart(name: key, content: value)
            }
        }

        return body

    }

    private void checkConfig(SendGridEmail email) {
        if ((!config.getProperty('sendgrid.password') || !config.getProperty('sendgrid.username')) &&
            (!email.username || !email.password)) {
            throw new MissingCredentialsException()
        }
    }

    private List<BodyPart> explode(String key, List entries) {

        return entries.collect { entry ->
            new BodyPart(name: key, content: entry)
        }

    }

    private Config getConfig() {
        grailsApplication.config
    }
}

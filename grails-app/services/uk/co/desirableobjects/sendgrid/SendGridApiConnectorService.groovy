package uk.co.desirableobjects.sendgrid

import wslite.rest.*
import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import org.codehaus.groovy.grails.commons.GrailsApplication

// TODO: Non 200 should return a response object!
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

        def proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress('localhost', 8888))

        RESTClient sendGrid = new RESTClient(grailsApplication.config.sendgrid?.api?.url ?: 'https://sendgrid.com/api/')
        Response response = sendGrid.post(proxy: proxy, path: 'mail.send.json') {
            prepareParameters(email).each { BodyPart part ->
                multipart part.name, part.content
            }
        }
        return handle(response)

    }
    
    private SendGridResponse handle(Response clientResponse) {

        return SendGridResponse.parse(clientResponse)

    }
    
    private List<BodyPart> prepareParameters(SendGridEmail email) {

        checkConfig()

        List<BodyPart> body = []
        body << new BodyPart(name: 'api_user', content: grailsApplication.config.sendgrid?.username.bytes)
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

    private void checkConfig() {
        if (!grailsApplication.config.sendgrid.password || !grailsApplication.config.sendgrid.username) {
            throw new MissingCredentialsException()
        }
    }

    private List<BodyPart> explode(String key, List entries) {

        return entries.collect { entry ->
            new BodyPart(name: key, content: entry)
        }

    }

}

package uk.co.desirableobjects.sendgrid
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import groovyx.net.http.RESTClient
import groovyx.net.http.ContentType
import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import groovyx.net.http.HttpResponseException

class SendGridApiConnectorService {

    private RESTClient sendGrid = new RESTClient(CH.config.sendgrid?.api?.url ?: 'https://sendgrid.com/api/')

    def post(SendGridEmail email) {

        def response = sendGrid.post(
            path: 'mail.send.json',
            body: prepareParameters(email),
            requestContentType: ContentType.URLENC,
        )
        handle(response)

    }
    
    private SendGridResponse handle(clientResponse) {

        return SendGridResponse.parse(clientResponse.data)

    }
    
    Map<String, Object> prepareParameters(SendGridEmail email) {

        if (!CH.config.sendgrid.password || !CH.config.sendgrid.username) {
            throw new MissingCredentialsException()
        }

        Map<String, Object> parameters = email.toMap()
        parameters.put('api_user', CH.config.sendgrid?.username)
        parameters.put('api_key', CH.config.sendgrid?.password)

        return parameters

    }

}

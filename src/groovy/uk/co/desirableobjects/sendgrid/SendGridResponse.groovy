package uk.co.desirableobjects.sendgrid

import org.codehaus.groovy.grails.web.json.JSONElement
import grails.converters.JSON

class SendGridResponse {

    boolean successful
    List<String> errors

    static SendGridResponse parse(def response) {

        JSONElement json = (JSONElement)JSON.parse(response.contentAsString as String)

        boolean success = determineResult(json)
        List<String> errorMessages = parseErrorMessages(json)

        return new SendGridResponse(successful: success, errors: errorMessages)

    }
    
    boolean hasErrors() {
        return !errors.isEmpty()
    }

    private static boolean determineResult(JSONElement response) {
        return (response.message == 'success')
    }

    private static List<String> parseErrorMessages(JSONElement response) {
        return response.errors*.toString()
    }

}

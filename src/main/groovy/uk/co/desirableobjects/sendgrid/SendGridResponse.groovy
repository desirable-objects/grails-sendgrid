package uk.co.desirableobjects.sendgrid

import grails.converters.JSON
import org.grails.web.json.JSONElement

class SendGridResponse {

    boolean successful
    List<String> errors

    static SendGridResponse parse(response) {

        JSONElement json = (JSONElement) JSON.parse(response.contentAsString as String)

        boolean success = determineResult(json)
        List<String> errorMessages = parseErrorMessages(json)

        return new SendGridResponse(successful: success, errors: errorMessages)

    }

    boolean hasErrors() {
        errors
    }

    private static boolean determineResult(JSONElement response) {
        return (response.message == 'success')
    }

    private static List<String> parseErrorMessages(JSONElement response) {
        return response.errors*.toString()
    }

}

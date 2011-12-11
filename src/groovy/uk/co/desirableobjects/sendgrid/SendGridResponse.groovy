package uk.co.desirableobjects.sendgrid

import org.codehaus.groovy.grails.web.json.JSONElement

class SendGridResponse {

    boolean successful
    List<String> errors

    static SendGridResponse parse(JSONElement response) {

        boolean success = determineResult(response)
        List<String> errorMessages = parseErrorMessages(response)

        return new SendGridResponse(successful: success, errors: errorMessages)

    }
    
    boolean hasErrors() {
        return !errors.isEmpty()
    }

    private static boolean determineResult(JSONElement response) {
        return (response.message == 'success')
    }

    private static List<String> parseErrorMessages(Object response) {
        return response.errors*.toString()
    }

}

package uk.co.desirableobjects.sendgrid

import org.codehaus.groovy.grails.web.json.JSONElement
import net.sf.json.JSONObject
import net.sf.json.JSON

class SendGridResponse {

    boolean successful
    List<String> errors

    static SendGridResponse parse(JSON response) {

        boolean success = determineResult(response)
        List<String> errorMessages = parseErrorMessages(response)

        return new SendGridResponse(successful: success, errors: errorMessages)

    }
    
    boolean hasErrors() {
        return !errors.isEmpty()
    }

    private static boolean determineResult(JSON response) {
        return (response.message == 'success')
    }

    private static List<String> parseErrorMessages(Object response) {
        return response.errors*.toString()
    }

}

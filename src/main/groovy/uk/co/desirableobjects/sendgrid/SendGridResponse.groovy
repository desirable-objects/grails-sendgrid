package uk.co.desirableobjects.sendgrid

import grails.converters.JSON
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.web.json.JSONElement

@CompileStatic
class SendGridResponse {

    boolean successful
    List<String> errors

    static SendGridResponse parse(response) {

        JSONElement json = (JSONElement) JSON.parse(contentAsString(response))

        boolean success = determineResult(json)
        List<String> errorMessages = parseErrorMessages(json)

        return new SendGridResponse(successful: success, errors: errorMessages)

    }

    @CompileDynamic
    private static String contentAsString(response) {
        response.contentAsString as String
    }

    boolean hasErrors() {
        errors
    }

    private static boolean determineResult(JSONElement response) {
        return (response['message'] == 'success')
    }

    private static List<String> parseErrorMessages(JSONElement response) {
        return (response['errors'] as List)*.toString()
    }

}

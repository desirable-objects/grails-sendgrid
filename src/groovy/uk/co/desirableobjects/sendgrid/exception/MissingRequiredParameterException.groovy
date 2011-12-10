package uk.co.desirableobjects.sendgrid.exception

import uk.co.desirableobjects.sendgrid.MailParameter

class MissingRequiredParameterException extends RuntimeException {

    MissingRequiredParameterException(List<MailParameter> parameters) {
        String parameterList = parameters*.name().join(', ')
        super(String.format("Missing required parameters: %s", parameterList))
    }

}

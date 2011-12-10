package uk.co.desirableobjects.sendgrid.exception
class InvalidRequestException extends RuntimeException {

    InvalidRequestException(String property) {
        super(String.format("Invalid sendgrid call. Property %s had an invalid value, or was missing", property))
    }

}

package uk.co.desirableobjects.sendgrid.exception

class InvalidEmailException extends RuntimeException {

    InvalidEmailException(def properties) {
        super(String.format("Invalid sendgrid email. Property %s had an invalid value, or was missing", properties))
    }

}

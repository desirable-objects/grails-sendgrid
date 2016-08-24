package uk.co.desirableobjects.sendgrid.exception

import groovy.transform.CompileStatic

@CompileStatic
class InvalidEmailException extends RuntimeException {

    InvalidEmailException(properties) {
        super(String.format("Invalid sendgrid email. Property %s had an invalid value, or was missing", properties))
    }

}

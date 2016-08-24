package uk.co.desirableobjects.sendgrid.exception

import groovy.transform.CompileStatic

@CompileStatic
class MissingCredentialsException extends RuntimeException {

    MissingCredentialsException() {
        super("Missing login credentials for SendGrid")
    }

}

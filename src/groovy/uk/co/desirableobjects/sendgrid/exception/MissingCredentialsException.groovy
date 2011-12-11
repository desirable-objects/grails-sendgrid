package uk.co.desirableobjects.sendgrid.exception

class MissingCredentialsException extends RuntimeException {

    MissingCredentialsException() {
        super("Missing login credentials for SendGrid")
    }

}

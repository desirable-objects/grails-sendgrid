package uk.co.desirableobjects.sendgrid

import groovy.transform.CompileStatic

@CompileStatic
class SendGridService {

    static transactional = false

    SendGridApiConnectorService sendGridApiConnectorService

    SendGridResponse send(SendGridEmail email) {

        sendGridApiConnectorService.post(email)

    }

    SendGridResponse sendMail(Closure closure) {

        SendGridSendMailDSLDelegate delegate = new SendGridSendMailDSLDelegate()

        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.call()

        send(delegate.build())

    }

}

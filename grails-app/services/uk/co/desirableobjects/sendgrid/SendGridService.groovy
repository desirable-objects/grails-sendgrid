package uk.co.desirableobjects.sendgrid

class SendGridService {

    SendGridApiConnectorService sendGridApiConnectorService

    def send(SendGridEmail email) {

        sendGridApiConnectorService.post(email)

    }

    def sendMail(Closure closure) {

        SendGridSendMailDSLDelegate delegate = new SendGridSendMailDSLDelegate()

        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.call()

        send(delegate.build())

    }

}

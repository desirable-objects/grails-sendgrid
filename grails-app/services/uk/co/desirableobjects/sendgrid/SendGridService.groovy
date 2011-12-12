package uk.co.desirableobjects.sendgrid

class SendGridService {

    SendGridApiConnectorService sendGridApiConnectorService

    def send(SendGridEmail email) {

        sendGridApiConnectorService.post(email)

    }

}

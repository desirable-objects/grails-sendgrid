package uk.co.desirableobjects.sendgrid

class SendgridService {

    SendGridApiConnectorService sendGridApiConnectorService

    def send(Map<String, Object> email) {

        sendGridApiConnectorService.post(email)

    }

}

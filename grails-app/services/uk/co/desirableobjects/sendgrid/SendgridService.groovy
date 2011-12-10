package uk.co.desirableobjects.sendgrid

import org.apache.commons.lang.NotImplementedException

class SendgridService {

    SendgridApiConnectorService sendgridApiConnectorService

    def send(Map<String, Object> email) {

        sendgridApiConnectorService.post(email)

    }

}

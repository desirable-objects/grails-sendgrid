package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.UnitSpec
import spock.lang.Shared
import org.springframework.http.HttpMethod

class SendgridServiceSpec extends UnitSpec {

    private static final String RECIPIENT = 'recipient@example.org'
    private static final String SENDER = 'sender@example.org'
    private static final String MESSAGE_TEXT = 'This is a test'
    private static final String SUBJECT = 'Hello there'

    @Shared SendgridService sendgridService
    @Shared SendgridApiConnectorService api = new MockSendgridConnector()

    def 'Send an email'() {

        given:
            sendgridService = new SendgridService()
            sendgridService.sendgridApiConnectorService = api

        when:
            sendgridService.send(to: RECIPIENT, subject:SUBJECT, text:MESSAGE_TEXT, from:SENDER)

        then:
            api.method == 'post'
            api.to == RECIPIENT
            api.subject == SUBJECT
            api.text == MESSAGE_TEXT
            api.from == SENDER

    }

    def 'Check for required parameters'() {
        
        given:
            List<String> required = ['to', 'subject', ['text', 'html'], 'from']
        
        when:
            sendgridService.send(params)
        
        then:
            thrown

    }

}

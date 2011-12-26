package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.UnitSpec
import spock.lang.Shared

class SendGridServiceSpec extends UnitSpec {

    private static final String RECIPIENT = 'recipient@example.org'
    private static final String RECIPIENT2 = 'recipient@example.com'
    private static final String BCC = 'bcc@example.net'
    private static final String SENDER = 'sender@example.org'
    private static final String MESSAGE_TEXT = 'This is a test'
    private static final String SUBJECT = 'Hello there'

    @Shared SendGridService sendGridService
    @Shared SendGridApiConnectorService api

    def setupSpec() {
        mockConfig '''
            sendgrid {
                username = 'test'
                password = 'test'
            }
        '''
        api = new MockSendGridConnector()
        sendGridService = new SendGridService()
        sendGridService.sendGridApiConnectorService = api
    }

    def 'Send an email'() {

        when:
            sendGridService.send(new SendGridEmail(to: RECIPIENT, subject:SUBJECT, body:MESSAGE_TEXT, from:SENDER))

        then:
            api.method == 'post'
            api.to == [RECIPIENT]
            api.subject == SUBJECT
            api.text == MESSAGE_TEXT
            api.from == SENDER

    }
    
    def 'Send a text email using the mail plugin DSL'() {
        
        when:
            sendGridService.sendMail {
                to this.RECIPIENT
                from this.SENDER
                subject this.SUBJECT
                body this.MESSAGE_TEXT
            }

        then:
            api.method == 'post'
            api.to == [RECIPIENT]
            api.subject == SUBJECT
            api.text == MESSAGE_TEXT
            api.from == SENDER
        
    }

    def 'Send a html email using the mail plugin DSL'() {

        when:
            sendGridService.sendMail {
                to this.RECIPIENT
                from this.SENDER
                bcc this.BCC
                subject this.SUBJECT
                html this.MESSAGE_TEXT
            }

        then:
            api.method == 'post'
            api.to == [RECIPIENT]
            api.subject == SUBJECT
            api.html == MESSAGE_TEXT
            api.from == SENDER
            api.bcc == [BCC]

    }

    def 'Send a text email with multiple recipients'() {

        when:
        sendGridService.sendMail {
            to this.RECIPIENT, this.RECIPIENT2
            from this.SENDER
            subject this.SUBJECT
            body this.MESSAGE_TEXT
        }

        then:
        api.method == 'post'
        api.to == [RECIPIENT, RECIPIENT2]
        api.subject == SUBJECT
        api.text == MESSAGE_TEXT
        api.from == SENDER

    }

}

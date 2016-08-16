package uk.co.desirableobjects.sendgrid

import grails.test.mixin.integration.Integration
import spock.lang.Ignore
import spock.lang.Specification

@Integration
class EmailAttachmentsIntegrationSpec extends Specification {

    SendGridService sendGridService
    def grailsApplication

    @Ignore
    def 'send an email with an attachment'() {

        given:
            grailsApplication.config.sendgrid = [username: 'your-username', password: 'your-password']

        and:
            SendGridResponse resp = sendGridService.sendMail {
                    to 'aj@incestry.co.uk'
                    from 'aj@incestry.co.uk'
                    subject 'hi'
                    body 'hello'
                    attach 'true.png', new File('test/unit/true.png')
                } as SendGridResponse

        expect:
            resp.successful
            resp.errors == []

    }

}

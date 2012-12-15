package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Ignore

class EmailAttachmentsIntegrationSpec extends IntegrationSpec {

    SendGridService sendGridService
    GrailsApplication grailsApplication

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
                    attach new File('test/unit/true.png')
                }

        expect:
            resp.successful
            resp.errors == []

    }

}

package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.UnitSpec

class MailParameterSpec extends UnitSpec {

    def 'check that required parameters are required'() {

        expect:
            !MailParameter.hasAllRequiredParameters(['to', 'subject', 'from'])
            MailParameter.hasAllRequiredParameters(['to', 'subject', 'from', 'text'])
            MailParameter.hasAllRequiredParameters(['to', 'subject', 'from', 'html'])

    }

}

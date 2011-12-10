package uk.co.desirableobjects.sendgrid

class MailParameterSpec {

    def 'check that required parameters are required'() {

        expect:
            !MailParameter.hasAllRequiredParameters(['to', 'subject', 'from'])
            MailParameter.hasAllRequiredParameters(['to', 'subject', 'from', 'text'])
            MailParameter.hasAllRequiredParameters(['to', 'subject', 'from', 'html'])

    }

    def 'check '

}

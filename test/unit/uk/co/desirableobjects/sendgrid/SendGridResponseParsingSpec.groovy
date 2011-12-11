package uk.co.desirableobjects.sendgrid

import org.codehaus.groovy.grails.web.json.JSONElement
import grails.converters.JSON
import grails.plugin.spock.UnitSpec
import spock.lang.Unroll

class SendGridResponseParsingSpec extends UnitSpec {

    def 'Parse a success response'() {

        given:
            JSONElement response = JSON.parse('{"message":"success"}')

        when:
            SendGridResponse sgResponse = SendGridResponse.parse(response)

        then:
            sgResponse.successful

    }

    def 'Parse an error response'() {

        given:
            JSONElement response = JSON.parse('{"message":"error","errors":["error message 1", "error message 2"]}')

        when:
            SendGridResponse sgResponse = SendGridResponse.parse(response)

        then:
            !sgResponse.successful
            sgResponse.hasErrors()
            sgResponse.errors.size() == 2
            sgResponse.errors[0] == 'error message 1'
            sgResponse.errors[1] == 'error message 2'

    }

}

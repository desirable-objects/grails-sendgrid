package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.UnitSpec
import net.sf.json.JSON
import net.sf.json.groovy.GJson
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer

class SendGridResponseParsingSpec extends UnitSpec {

    def 'Parse a success response'() {

        given:
            JSON response = JSONSerializer.toJSON('{"message":"success"}')

        when:
            SendGridResponse sgResponse = SendGridResponse.parse(response)

        then:
            sgResponse.successful

    }

    def 'Parse an error response'() {

        given:
            JSON response = JSONSerializer.toJSON('{"message":"error","errors":["error message 1", "error message 2"]}')

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

package uk.co.desirableobjects.sendgrid

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import wslite.http.HTTPRequest
import wslite.http.HTTPResponse
import wslite.rest.Response

@TestMixin(GrailsUnitTestMixin)
class SendGridResponseParsingSpec extends Specification {

    void 'Parse a success response'() {

        given:
            Response response = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
            SendGridResponse sgResponse = SendGridResponse.parse(response)

        then:
            response.contentAsString >> { return '{"message":"success"}' }

        and:
            sgResponse.successful

    }

    void 'Parse an error response'() {

        given:
            Response response = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
            SendGridResponse sgResponse = SendGridResponse.parse(response)

        then:
            response.contentAsString >> { return '{"message":"error","errors":["error message 1", "error message 2"]}' }

        then:
            !sgResponse.successful
            sgResponse.hasErrors()
            sgResponse.errors.size() == 2
            sgResponse.errors[0] == 'error message 1'
            sgResponse.errors[1] == 'error message 2'

    }

}

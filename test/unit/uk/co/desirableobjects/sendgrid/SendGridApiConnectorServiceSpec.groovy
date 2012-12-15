package uk.co.desirableobjects.sendgrid

import org.springframework.beans.BeanUtils
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientException
import spock.lang.Shared

import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import spock.lang.Unroll
import net.sf.json.JSONSerializer
import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.test.mixin.support.GrailsUnitTestMixin
import uk.co.desirableobjects.sendgrid.exception.SendGridCommunicationException
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response
import wslite.http.HTTPRequest
import wslite.http.HTTPResponse

@Mixin(GrailsUnitTestMixin)
@TestFor(SendGridApiConnectorService)
class SendGridApiConnectorServiceSpec extends Specification {

    private static final String USERNAME = 'antony'
    private static final String PASSWORD = 'password'

    private static final Map<String, String> DEFAULT_CREDENTIALS = [username: USERNAME, password: PASSWORD]

    private static Response mockResponse
    private static Map<String, Object> postData = [:]
    private static String apiUrl

    private class MockRestClientDelegate {

        def multipart = { String name, byte[] body ->
            postData.put(name, new String(body, 'UTF-8'))
        }

    }

    void setup() {

        RESTClient.metaClass.constructor = { String url ->
            apiUrl = url
            BeanUtils.instantiateClass(RESTClient)
        }

        RESTClient.metaClass.post = { Map params, Closure closure ->
            closure.delegate = new MockRestClientDelegate()
            closure()
            return mockResponse
        }

    }
    
    def 'connector provides username and password to api'() {

        given:
            grailsApplication.config.sendgrid = DEFAULT_CREDENTIALS

        and:
            mockResponse = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
            service.post(new SendGridEmail())

        then:
            1 * mockResponse.contentAsString >> { return "{'status':'ok'}" }
            0 * _

        and:
            postData.api_user == USERNAME
            postData.api_key == PASSWORD

    }

    @Unroll
    def 'No authentication configured [configuration was #conf]'() {

        given:
            grailsApplication.config = conf

        when:
            service.post(new SendGridEmail())

        then:
            thrown MissingCredentialsException

        where:
            conf << [[:], [sendgrid:[:]], [sendgrid: [username:null]]]

    }

    @Unroll
    def 'Configuration #conf overrides API URL to be #expectedUri'() {

        given:
            service.grailsApplication.config = conf

        and:
            mockResponse = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
            service.post(new SendGridEmail())

        then:
            1 * mockResponse.contentAsString >> { return "{'status':'ok'}" }
            0 * _

        and:
            apiUrl == expectedUri

        where:
            conf                                                                | expectedUri
            [sendgrid: [:] + DEFAULT_CREDENTIALS]                               | 'https://sendgrid.com/api/'
            [sendgrid: [api:[url:'http://example.net']] + DEFAULT_CREDENTIALS]  | 'http://example.net'

    }

    def 'connector can post attachments in the correct format'() {

        given:
            File file = new File('test/unit/true.png')
            grailsApplication.config.sendgrid = DEFAULT_CREDENTIALS

        and:
            mockResponse = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
            service.post(new SendGridEmail(attachments: [file]))

        then:
            1 * mockResponse.contentAsString >> { return "{'status':'ok'}" }
            0 * _

        and:
            postData["files[true.png]"] == new String(file.bytes, 'UTF-8')

    }

    def 'send mail receives an exception'() {

        setup:
            HTTPResponse mockResponse = Mock(HTTPResponse)
            RESTClient.metaClass.post = { Map params, Closure closure ->
                throw new RESTClientException('problem', new HTTPRequest(), mockResponse)
            }

        and:
            grailsApplication.config.sendgrid = DEFAULT_CREDENTIALS

        when:
            SendGridResponse sendGridResponse = service.post(new SendGridEmail())

        then:
            1 * mockResponse.contentAsString >> { return '''{
    "message": "error",
    "errors": [
        "Permission denied, wrong credentials"
    ]
}''' }
            0 * _

        and:
            !sendGridResponse.successful
            sendGridResponse.hasErrors()
            sendGridResponse.errors.first() == 'Permission denied, wrong credentials'

        cleanup:
            RESTClient.metaClass = null

    }

    def 'send mail receives an exception when there is no response'() {

        setup:
            RESTClient.metaClass.post = { Map params, Closure closure ->
                throw new RESTClientException('problem', new HTTPRequest(), null)
            }

        and:
            grailsApplication.config.sendgrid = DEFAULT_CREDENTIALS

        when:
            service.post(new SendGridEmail())

        then:
            thrown SendGridCommunicationException

        cleanup:
            RESTClient.metaClass = null

    }

}

package uk.co.desirableobjects.sendgrid

import grails.test.mixin.TestFor
import org.springframework.beans.BeanUtils
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.desirableobjects.sendgrid.exception.MissingCredentialsException
import uk.co.desirableobjects.sendgrid.exception.SendGridCommunicationException
import wslite.http.HTTPRequest
import wslite.http.HTTPResponse
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

@TestFor(SendGridApiConnectorService)
class SendGridApiConnectorServiceSpec extends Specification {

    private static final String USERNAME = 'antony'
    private static final String PASSWORD = 'password'

    private static final LinkedHashMap DEFAULT_CREDENTIALS = [username: USERNAME, password: PASSWORD]

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

    def 'connector provides username and password to api from configuration'() {

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

    def 'connector provides username and password to api from email'() {

        given:
        grailsApplication.config = new ConfigObject()

        and:
        mockResponse = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
        service.post(new SendGridEmail(username: 'testuser', password: 'testpassword'))

        then:
        1 * mockResponse.contentAsString >> { return "{'status':'ok'}" }
        0 * _

        and:
        postData.api_user == 'testuser'
        postData.api_key == 'testpassword'

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
        conf << [new CustomConfigObject([:]), new CustomConfigObject([sendgrid: [:]]), new CustomConfigObject([sendgrid: [username: null]])]

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
        conf                                                                                         | expectedUri
        new CustomConfigObject([sendgrid: [:] + DEFAULT_CREDENTIALS])                                | 'https://sendgrid.com/api/'
        new CustomConfigObject([sendgrid: [api: [url: 'http://example.net']] + DEFAULT_CREDENTIALS]) | 'http://example.net'

    }

    def 'connector can post attachments in the correct format'() {

        given:
        File file = new File('src/test/groovy/true.png')
        grailsApplication.config.sendgrid = DEFAULT_CREDENTIALS

        and:
        mockResponse = Mock(Response, constructorArgs: [Mock(HTTPRequest), Mock(HTTPResponse)])

        when:
        service.post(new SendGridEmail(attachments: ['true.png': file]))

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
        1 * mockResponse.contentAsString >> {
            return '''{
    "message": "error",
    "errors": [
        "Permission denied, wrong credentials"
    ]
}'''
        }
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

    /**
     * Customizes groovy's config object to construct one from a LinkedHashMap
     */
    static class CustomConfigObject extends ConfigObject {
        CustomConfigObject(LinkedHashMap map) {
            putAll(map)
        }
    }
}

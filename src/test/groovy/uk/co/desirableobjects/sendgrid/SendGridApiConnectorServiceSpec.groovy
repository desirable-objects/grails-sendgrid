package uk.co.desirableobjects.sendgrid

import grails.config.Config
import grails.test.mixin.TestFor
import org.grails.config.PropertySourcesConfig
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

    private static final Map DEFAULT_CREDENTIALS = [username: USERNAME, password: PASSWORD]

    private static Response mockResponse
    private static Map<String, Object> postData = [:]
    private static String apiUrl

    private class MockRestClientDelegate {

        def multipart = { String name, byte[] body ->
            postData[name] = new String(body, 'UTF-8')
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

    void 'connector provides username and password to api from configuration'() {

        given:
        grailsApplication.config = configWithDefaultCredentials()

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

    void 'connector provides username and password to api from email'() {

        given:
        grailsApplication.config = new PropertySourcesConfig([:])

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
    void 'No authentication configured [configuration was #conf]'() {

        given:
        grailsApplication.config = conf

        when:
        service.post(new SendGridEmail())

        then:
        thrown MissingCredentialsException

        where:
        conf << [new PropertySourcesConfig([:]), new PropertySourcesConfig([sendgrid: [:]]), new PropertySourcesConfig([sendgrid: [username: null]])]

    }

    @Unroll
    void 'Configuration #conf overrides API URL to be #expectedUri'() {

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
        conf                                                                                            | expectedUri
        new PropertySourcesConfig([sendgrid: [:] + DEFAULT_CREDENTIALS])                                | 'https://sendgrid.com/api/'
        new PropertySourcesConfig([sendgrid: [api: [url: 'http://example.net']] + DEFAULT_CREDENTIALS]) | 'http://example.net'

    }

    void 'connector can post attachments in the correct format'() {

        given:
        File file = new File('src/test/groovy/true.png')
        grailsApplication.config = configWithDefaultCredentials()

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

    void 'send mail receives an exception'() {

        setup:
        HTTPResponse mockResponse = Mock(HTTPResponse)
        RESTClient.metaClass.post = { Map params, Closure closure ->
            throw new RESTClientException('problem', new HTTPRequest(), mockResponse)
        }

        and:
        grailsApplication.config = configWithDefaultCredentials()

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

    void 'send mail receives an exception when there is no response'() {

        setup:
        RESTClient.metaClass.post = { Map params, Closure closure ->
            throw new RESTClientException('problem', new HTTPRequest(), null)
        }

        and:
        grailsApplication.config = configWithDefaultCredentials()

        when:
        service.post(new SendGridEmail())

        then:
        thrown SendGridCommunicationException

        cleanup:
        RESTClient.metaClass = null

    }

    private Config configWithDefaultCredentials() {
        new PropertySourcesConfig(sendgrid: [:] + DEFAULT_CREDENTIALS)
    }
}

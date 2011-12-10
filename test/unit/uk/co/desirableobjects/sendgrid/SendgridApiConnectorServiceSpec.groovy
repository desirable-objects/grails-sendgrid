package uk.co.desirableobjects.sendgrid

import grails.plugin.spock.UnitSpec
import spock.lang.Shared

class SendgridApiConnectorServiceSpec extends UnitSpec {

    @Shared SendgridApiConnectorService sendgridApiConnectorService
    
    private static final String USERNAME = 'antony'
    private static final String PASSWORD = 'password'

    def 'connector provides username and password to api'() {

        given:
            mockConfig """
                sendgrid {
                    username = ${USERNAME}
                    password = ${PASSWORD}
                }
            """

        when:
            sendgridApiConnectorService.post([:])
        
        then:
            api.api_user == USERNAME
            api.api_key == PASSWORD

        

    }

}

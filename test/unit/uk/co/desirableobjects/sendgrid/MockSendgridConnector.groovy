package uk.co.desirableobjects.sendgrid

class MockSendgridConnector extends SendgridApiConnectorService {

    Map lastCall = [:]

    @Override
    def post(Map<String, Object> params) {
        
        lastCall.method = 'post'
        lastCall.putAll(params)
    }

    def propertyMissing(String name) { lastCall[name] }

}

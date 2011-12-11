package uk.co.desirableobjects.sendgrid

class MockSendGridConnector extends SendGridApiConnectorService {

    Map lastCall = [:]

    @Override
    def post(Map<String, String> params) {
        
        lastCall.method = 'post'
        lastCall.putAll(params)
    }

    def propertyMissing(String name) { lastCall[name] }

}

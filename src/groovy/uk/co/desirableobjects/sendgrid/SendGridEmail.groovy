package uk.co.desirableobjects.sendgrid

class SendGridEmail {

    String recipient
    String recipientName
    String sender
    String subject
    String text
    String html

    private optionalParameterMappings = [
            text:'text',
            html:'html',
            recipientName:'toname'
    ]

    Map<String, String> toMap() {

        Map<String, String> parameters = [
                to: recipient,
                from: sender,
                subject: subject
        ]

        optionalParameterMappings.each { String internalName, String externalName ->
            if (this[internalName]) {
                parameters.put(externalName, this[internalName] as String)
            }
        }

        return parameters

    }

}

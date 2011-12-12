package uk.co.desirableobjects.sendgrid

class SendGridEmail {

    String recipient
    String sender
    String subject
    String text
    String html

    Map<String, String> toMap() {

        Map<String, String> parameters = [
                to: recipient,
                from: sender,
                subject: subject
        ]

        ['text', 'html'].each { String property ->
            if (this[property]) {
                parameters.put(property, this["${property}"] as String)
            }
        }

        return parameters

    }

}

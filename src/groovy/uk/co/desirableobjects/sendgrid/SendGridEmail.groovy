package uk.co.desirableobjects.sendgrid

import grails.converters.JSON

class SendGridEmail {

    String recipient
    String recipientName
    String sender
    String senderName
    String subject
    String text
    String html
    String replyTo
    Date sentDate
    List<String> bcc = []
    Map headers = [:]
    Map customHandlingInstructions = [:]

    private optionalParameterMappings = [
            text:'text',
            html:'html',
            recipientName:'toname',
            bcc:'bcc',
            replyTo:'replyto',
            sentDate:'date',
            senderName:'fromname',
            headers:'headers',
            customHandlingInstructions:'x-smtpapi'
    ]

    Map<String, String> toMap() {

        Map<String, String> parameters = [
                to: recipient,
                from: sender,
                subject: subject
        ]

        optionalParameterMappings.each { String internalName, String externalName ->
            if (this[internalName]) {
                parameters.put(externalName, map(this[internalName]))
            }
        }

        return parameters

    }

    private String map(String string) {
        return string
    }

    private String map(List<String> list) {
        return "[\"${list.join("\",\"")}\"]"
    }

    private String map(Date date) {
        return date.format("yyyy-MM-dd'T'HH:mm:ssz")
    }

    private String map(HashMap map) {
        return (map as JSON).toString()
    }

}

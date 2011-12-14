package uk.co.desirableobjects.sendgrid

import grails.converters.JSON

class SendGridEmail {

    List<String> to = []
    List<String> toName = []
    String from
    String fromName
    String subject
    String body
    String html
    String replyTo
    Date sentDate
    List<String> bcc = []
    Map headers = [:]
    Map customHandlingInstructions = [:]

    private optionalParameterMappings = [
            body:'text',
            html:'html',
            toName:'toname',
            bcc:'bcc',
            replyTo:'replyto',
            sentDate:'date',
            fromName:'fromname',
            headers:'headers',
            customHandlingInstructions:'x-smtpapi'
    ]

    Map<String, String> toMap() {

        Map<String, String> parameters = [
                to: map(this['to']),
                from: from,
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
    
    public void setTo(String recipient) {
        this.to << recipient
    }

}

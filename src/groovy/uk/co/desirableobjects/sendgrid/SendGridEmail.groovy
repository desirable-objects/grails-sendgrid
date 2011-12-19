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
    List<File> attachments = []

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

        parameters.putAll(encodeOptionalParameters())
        parameters.putAll(addAttachments())

        return parameters

    }
    
    private Map<String, String> encodeOptionalParameters() {
        
        Map<String, String> parameters = [:]
        
        optionalParameterMappings.each { String internalName, String externalName ->
            if (this[internalName]) {
                parameters.put(externalName, map(this[internalName]))
            }
        }
        
        return parameters
    }
    
    private Map<String, String> addAttachments() {
        
        Map<String, String> parameters = [:]
        
        attachments.each { File attachment ->
            parameters.put("files[${attachment.name}]" as String, URLEncoder.encode(attachment.text))
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

package uk.co.desirableobjects.sendgrid

import grails.converters.JSON
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.net.URLCodec

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

    private allParameters = [
            to:'to',
            from:'from',
            subject:'subject',
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

    Map<String, Object> toMap() {

        Map<String, Object> parameters = [:]

        parameters.putAll(encodeParameters())
        parameters.putAll(addAttachments())

        return parameters

    }
    
    private Map<String, Object> encodeParameters() {
        
        Map<String, Object> parameters = [:]
        
        allParameters.each { String internalName, String externalName ->
            Object value = this[internalName]
            if (value) {
                parameters.put(externalName, map(value))
            }
        }
        
        return parameters
    }

    private Map<String, Object> addAttachments() {
        
        Map<String, Object> parameters = [:]
        
        attachments.each { File attachment ->
            parameters.put("files[${attachment.name}]" as String, new String(attachment.bytes))
        }

        return parameters
    }

    private List<String> map(List<String> values) {
        return values
    }

    private String map(String string) {
        return string
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

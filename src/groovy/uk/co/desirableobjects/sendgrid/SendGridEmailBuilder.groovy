package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

class SendGridEmailBuilder {

    private SendGridEmail email

    private SendGridEmailBuilder(String senderName, String sender) {
        this.email = new SendGridEmail(fromName: senderName, from: sender)
    }
    
    static from(String senderName = null, String sender) {
        return new SendGridEmailBuilder(senderName, sender)
    }

    SendGridEmailBuilder to(String toName = null, String to) {
        if (toName) {
            email.toName << toName
        }
        email.to << to
        return this
    }

    SendGridEmailBuilder addRecipient(String toName = null, String to) {
        return this.to(toName, to)
    }

    SendGridEmailBuilder subject(String subjectLine) {
        email.subject = subjectLine
        return this
    }
    
    SendGridEmailBuilder withText(String textContent) {
        email.body = textContent
        return this
    }

    SendGridEmailBuilder withHtml(String htmlContent) {
        email.html = htmlContent
        return this
    }

    SendGridEmailBuilder addBcc(String bccEmail) {
        email.bcc << bccEmail
        return this
    }

    SendGridEmailBuilder replyTo(String replyAddress) {
        email.replyTo = replyAddress
        return this
    }

    SendGridEmailBuilder sentDate(Date date) {
        email.sentDate = date
        return this
    }

    SendGridEmailBuilder addHeader(String headerName, String headerValue) {
        email.headers.put(headerName, headerValue)
        return this
    }

    SendGridEmailBuilder addCustomHandlingInstruction(String headerName, String headerValue) {
        email.customHandlingInstructions.put(headerName, headerValue)
        return this
    }

    SendGridEmailBuilder addAttachment(File file) {
        email.attachments << file
        return this
    }

    SendGridEmail build() {
        validateRequiredParameters()
        return this.email
    }

    // TODO: Non verbose error message!
    private validateRequiredParameters() {
        if (!(this.email.from && this.email.subject && this.email.to && (this.email.body || this.email.html))) {
            throw new InvalidEmailException([])
        }
    }

}

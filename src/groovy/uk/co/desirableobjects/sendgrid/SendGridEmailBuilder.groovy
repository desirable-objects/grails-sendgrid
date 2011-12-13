package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

class SendGridEmailBuilder {

    private SendGridEmail email

    private SendGridEmailBuilder(String senderName, String sender) {
        this.email = new SendGridEmail(senderName: senderName, sender: sender)
    }
    
    static from(String senderName = null, String sender) {
        return new SendGridEmailBuilder(senderName, sender)
    }

    SendGridEmailBuilder to(String toName = null, String to) {
        email.recipientName = toName
        email.recipient = to
        return this
    }

    SendGridEmailBuilder subject(String subjectLine) {
        email.subject = subjectLine
        return this
    }
    
    SendGridEmailBuilder withText(String textContent) {
        email.text = textContent
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

    SendGridEmail build() {
        validateRequiredParameters()
        return this.email
    }

    // TODO: Non verbose error message!
    private validateRequiredParameters() {
        if (!(this.email.sender && this.email.subject && this.email.recipient && (this.email.text || this.email.html))) {
            throw new InvalidEmailException([])
        }
    }

}

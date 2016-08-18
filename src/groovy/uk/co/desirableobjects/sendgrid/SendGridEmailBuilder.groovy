package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

class SendGridEmailBuilder {

    private SendGridEmail email

    SendGridEmailBuilder() {
        this.email = new SendGridEmail()
    }

    SendGridEmailBuilder apiCredentials(String username, String password) {
        email.username = username
        email.password = password
        return this
    }

    SendGridEmailBuilder from(String senderName = null, String sender) {
        email.fromName = senderName
        email.from = sender
        return this
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

    SendGridEmailBuilder addCustomHandlingInstruction(String headerName, Object headerValue) {
        email.customHandlingInstructions.put(headerName, headerValue)
        return this
    }

    SendGridEmailBuilder addAttachment(String filename, file) {

        if (filename.contains('[') || filename.contains(']')) {
            throw new IllegalArgumentException('You cannot use square brackets in attachment filenames')
        }

        email.attachments.put(filename, file)
        return this
    }

    SendGridEmail build() {
        validateRequiredParameters()
        return this.email
    }

    private validateRequiredParameters() {

        ['to', 'subject', 'from'].each { String field ->
            if (!this.email[field]) {
                throw new InvalidEmailException([field])
            }
        }

        if (!(this.email.body || this.email.html)) {
            throw new InvalidEmailException(['body', 'html'])
        }
    }

}

package uk.co.desirableobjects.sendgrid

import groovy.transform.CompileStatic
import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

@CompileStatic
class SendGridEmailBuilder {

    private static final List<String> REQUIRED_PARAMETERS = ['to', 'subject', 'from']

    private SendGridEmail email = new SendGridEmail()

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

    SendGridEmailBuilder addRecipient(String toName = null, String recipient) {
        return to(toName, recipient)
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

    SendGridEmailBuilder addAttachment(String filename, File file) {

        if (filename.contains('[') || filename.contains(']')) {
            throw new IllegalArgumentException('You cannot use square brackets in attachment filenames')
        }

        email.attachments.put(filename, file)
        return this
    }

    SendGridEmail build() {
        validateRequiredParameters()
        return email
    }

    private validateRequiredParameters() {

        for (String field in REQUIRED_PARAMETERS) {
            if (!email[field]) {
                throw new InvalidEmailException([field])
            }
        }

        if (!(email.body || email.html)) {
            throw new InvalidEmailException(['body', 'html'])
        }
    }

}

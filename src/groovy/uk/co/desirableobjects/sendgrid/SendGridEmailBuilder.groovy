package uk.co.desirableobjects.sendgrid

import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

class SendGridEmailBuilder {

    private SendGridEmail email

    private SendGridEmailBuilder(String sender) {
        this.email = new SendGridEmail(sender: sender)
    }
    
    static from(String sender) {
        return new SendGridEmailBuilder(sender)
    }
    
    SendGridEmailBuilder to(String to) {
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

    SendGridEmail build() {

        if (!(this.email.sender && this.email.subject && this.email.recipient && (this.email.text || this.email.html))) {
            throw new InvalidEmailException(this.email.properties.findAll { !it } )
        }

        return this.email
    }

}

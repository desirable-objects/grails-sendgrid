package uk.co.desirableobjects.sendgrid

class SendGridSendMailDSLDelegate {

    SendGridEmailBuilder builder = new SendGridEmailBuilder()

    def methodMissing(String method, args) {

        return builder."${method}"(*args)

    }
    
    SendGridEmailBuilder to(String... recipients) {
        recipients.each { String recipient ->
            builder.to(recipient)
        }
        return builder
    }

    SendGridEmailBuilder body(String textContent) {
        return builder.withText(textContent)
    }

    SendGridEmailBuilder html(String htmlContent) {
        return builder.withHtml(htmlContent)
    }

    SendGridEmailBuilder bcc(String bccEmail) {
        return builder.addBcc(bccEmail)
    }

    @Deprecated
    SendGridEmailBuilder attach(File attachment) {
        return builder.addAttachment(attachment.name, attachment)
    }

    SendGridEmailBuilder attach(String filename, File attachment) {
        return builder.addAttachment(filename, attachment)
    }

}

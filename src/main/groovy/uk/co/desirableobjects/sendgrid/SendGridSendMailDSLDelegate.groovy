package uk.co.desirableobjects.sendgrid

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class SendGridSendMailDSLDelegate {

    SendGridEmailBuilder builder = new SendGridEmailBuilder()

    @CompileDynamic
    def methodMissing(String method, args) {
        return builder."$method"(*args)
    }

    SendGridEmailBuilder to(String... recipients) {
        for (recipient in recipients) {
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

    SendGridEmail build() {
        builder.build()
    }
}

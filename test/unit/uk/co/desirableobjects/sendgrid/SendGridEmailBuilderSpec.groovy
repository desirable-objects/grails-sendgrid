package uk.co.desirableobjects.sendgrid

import spock.lang.Unroll
import org.spockframework.compiler.model.Spec
import spock.lang.Specification
import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException


class SendGridEmailBuilderSpec extends Specification {

    private static final String RECIPIENT_EMAIL = 'antony@example.net'
    private static final String RECIPIENT_NAME = 'Antony Jones'
    private static final String SENDER_EMAIL = 'antony@example.com'

    def "Builder can build emails"() {

        given:
            SendGridEmail email = SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There').withText("What's up?").build()

        expect:
            email.sender == SENDER_EMAIL
            email.recipient == RECIPIENT_EMAIL
            email.subject == 'Hello There'
            email.text == "What's up?"

        when:
            email = SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There').withHtml("<h1>What's up?</h1>").build()

        then:
            email.sender == SENDER_EMAIL
            email.recipient == RECIPIENT_EMAIL
            email.subject == 'Hello There'
            email.html == "<h1>What's up?</h1>"

    }

    @Unroll('Builder attempts to build invalid email combination #email')
    def "Builder cannot build invalid emails"() {

        when:
            email.build()

        then:
            thrown InvalidEmailException

        where:
            email << [
                    SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There'),
                    SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).withText("What's up?"),
                    SendGridEmailBuilder.from(SENDER_EMAIL).subject('Hello There').withText("What's up?")
            ]

    }

    def 'Builder can take a recipient with a display name'() {

        given:
            SendGridEmailBuilder builder = SendGridEmailBuilder.from(SENDER_EMAIL).subject('Hello There').withHtml("<h1>What's up?</h1>")

        when:
            SendGridEmail email = builder.to(RECIPIENT_EMAIL).build()
            Map emailParameters = email.toMap()

        then:
            email.recipient == RECIPIENT_EMAIL
            emailParameters.to == RECIPIENT_EMAIL

        when:
            email = builder.to(RECIPIENT_NAME, RECIPIENT_EMAIL).build()
            emailParameters = email.toMap()

        then:
            email.recipient == RECIPIENT_EMAIL
            email.recipientName == RECIPIENT_NAME
            emailParameters.to == RECIPIENT_EMAIL
            emailParameters.toname == RECIPIENT_NAME

    }


}

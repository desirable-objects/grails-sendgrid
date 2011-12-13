package uk.co.desirableobjects.sendgrid

import spock.lang.Unroll
import org.spockframework.compiler.model.Spec
import spock.lang.Specification
import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException
import spock.lang.Shared
import grails.converters.JSON
import grails.plugin.spock.UnitSpec


class SendGridEmailBuilderSpec extends UnitSpec {

    private static final String RECIPIENT_EMAIL = 'antony@example.net'
    private static final String RECIPIENT_NAME = 'Antony Jones'
    private static final String SENDER_NAME = 'Johnny Sender'
    private static final String SENDER_EMAIL = 'antony@example.com'
    private static final String DEFAULT_TEXT_CONTENT = "What's up?"
    private static final String BCC_RECIPIENT = 'bcc@example.com'
    private static final String REPLY_TO_RECIPIENT = 'reply@example.com'
    private static final String ANOTHER_BCC_RECIPIENT = 'anotherbcc@example.org'

    SendGridEmailBuilder builder() {
        return SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There')
    }

    def "Builder can build emails"() {

        given:
            SendGridEmail email = builder().withText(DEFAULT_TEXT_CONTENT).build()

        expect:
            email.sender == SENDER_EMAIL
            email.recipient == RECIPIENT_EMAIL
            email.subject == 'Hello There'
            email.text == DEFAULT_TEXT_CONTENT

        when:
            email = builder().withHtml("<h1>What's up?</h1>").build()

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
                    SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).withText(DEFAULT_TEXT_CONTENT),
                    SendGridEmailBuilder.from(SENDER_EMAIL).subject('Hello There').withText(DEFAULT_TEXT_CONTENT)
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

    def 'Builder can take a sender with a display name'() {

        given:
            SendGridEmail email = SendGridEmailBuilder.from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There').withHtml("<h1>What's up?</h1>").build()

        when:
            Map emailParameters = email.toMap()

        then:
            email.sender == SENDER_EMAIL
            emailParameters.from == SENDER_EMAIL

        when:
            email = SendGridEmailBuilder.from(SENDER_NAME, SENDER_EMAIL).to(RECIPIENT_EMAIL).subject('Hello There').withHtml("<h1>What's up?</h1>").build()
            emailParameters = email.toMap()

        then:
            email.sender == SENDER_EMAIL
            email.senderName == SENDER_NAME
            emailParameters.from == SENDER_EMAIL
            emailParameters.fromname == SENDER_NAME

    }

    // TODO: BCC is an array? Explain!
    def 'Builder can add Bccs'() {

        given:
            SendGridEmail email = builder().withText(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).build()

        expect:
            email.bcc == [BCC_RECIPIENT]
            email.toMap().bcc == "[\"${BCC_RECIPIENT}\"]"
        
        when:
            email = builder().withText(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).addBcc(ANOTHER_BCC_RECIPIENT).build()

        then:
            email.bcc == [BCC_RECIPIENT, ANOTHER_BCC_RECIPIENT]
            email.toMap().bcc == "[\"${BCC_RECIPIENT}\",\"${ANOTHER_BCC_RECIPIENT}\"]"

    }

    def 'Builder correctly translates optional parameters'() {

        given:
            Date sentDate = new Date()
            SendGridEmail email = builder().withText(DEFAULT_TEXT_CONTENT).withHtml(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).replyTo(REPLY_TO_RECIPIENT).sentDate(sentDate).build()
            Map emailParameters = email.toMap()

        expect:
            email.bcc == [BCC_RECIPIENT]
            email.replyTo == REPLY_TO_RECIPIENT
            email.sentDate == sentDate

        and:
            emailParameters.bcc == "[\"${BCC_RECIPIENT}\"]"
            emailParameters.replyto == REPLY_TO_RECIPIENT
            emailParameters.date == sentDate.format("yyyy-MM-dd'T'HH:mm:ssz")

    }

    def 'Builder can add Headers'() {

        given:
            SendGridEmail email = builder().withText(DEFAULT_TEXT_CONTENT).addHeader('my-header', 'my-value').build()

        expect:
            email.headers == ['my-header': 'my-value']
            email.toMap().headers == (email.headers as JSON).toString()

        when:
            email = builder().withText(DEFAULT_TEXT_CONTENT).addHeader('a', '1').addHeader('b', '2').build()

        then:
            email.headers == [a:'1', b:'2']
            email.toMap().headers == (email.headers as JSON).toString()

    }

    def 'Builder can add Custom handling instructions'() {

        given:
            SendGridEmail email = builder().withText(DEFAULT_TEXT_CONTENT).addCustomHandlingInstruction('my-header', 'my-value').build()

        expect:
            email.customHandlingInstructions == ['my-header': 'my-value']
            email.toMap().'x-smtpapi' == (email.customHandlingInstructions as JSON).toString()

        when:
            email = builder().withText(DEFAULT_TEXT_CONTENT).addCustomHandlingInstruction('a', '1').addCustomHandlingInstruction('b', '2').build()

        then:
            email.customHandlingInstructions == [a:'1', b:'2']
            email.toMap().'x-smtpapi' == (email.customHandlingInstructions as JSON).toString()

    }

}

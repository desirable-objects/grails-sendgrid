package uk.co.desirableobjects.sendgrid

import spock.lang.Unroll

import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException

import grails.converters.JSON
import grails.plugin.spock.UnitSpec


class SendGridEmailBuilderSpec extends UnitSpec {

    private static final String RECIPIENT_EMAIL = 'antony@example.net'
    private static final String RECIPIENT_NAME = 'Antony Jones'
    private static final String ADDITIONAL_RECIPIENT_EMAIL = 'an.other@example.net'
    private static final String ADDITIONAL_RECIPIENT_NAME = 'Another Person'
    private static final String SENDER_NAME = 'Johnny Sender'
    private static final String SENDER_EMAIL = 'antony@example.com'
    private static final String DEFAULT_TEXT_CONTENT = "What's up?"
    private static final String BCC_RECIPIENT = 'bcc@example.com'
    private static final String REPLY_TO_RECIPIENT = 'reply@example.com'
    private static final String ANOTHER_BCC_RECIPIENT = 'anotherbcc@example.org'
    private static final String EXAMPLE_HTML = "<h1>What's up?</h1>"
    private static final String EXAMPLE_SUBJECT = 'Hello There'

    SendGridEmailBuilder createBuilder() {
        return new SendGridEmailBuilder().from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject(EXAMPLE_SUBJECT)
    }

    def "Builder can build emails"() {

        given:
            SendGridEmail email = createBuilder().withText(DEFAULT_TEXT_CONTENT).build()

        expect:
            email.from == SENDER_EMAIL
            email.to == [RECIPIENT_EMAIL]
            email.subject == EXAMPLE_SUBJECT
            email.body == DEFAULT_TEXT_CONTENT

        when:
            email = createBuilder().withHtml(EXAMPLE_HTML).build()

        then:
            email.from == SENDER_EMAIL
            email.to == [RECIPIENT_EMAIL]
            email.subject == EXAMPLE_SUBJECT
            email.html == EXAMPLE_HTML

    }

    @Unroll('Builder attempts to build invalid email combination #email')
    def "Builder cannot build invalid emails"() {

        when:
            email.build()

        then:
            thrown InvalidEmailException

        where:
            email << [
                    new SendGridEmailBuilder().from(SENDER_EMAIL).to(RECIPIENT_EMAIL).subject(EXAMPLE_SUBJECT),
                    new SendGridEmailBuilder().from(SENDER_EMAIL).to(RECIPIENT_EMAIL).withText(DEFAULT_TEXT_CONTENT),
                    new SendGridEmailBuilder().from(SENDER_EMAIL).subject(EXAMPLE_SUBJECT).withText(DEFAULT_TEXT_CONTENT)
            ]

    }

    def 'Builder can take a recipient with a display name'() {

        given:
            SendGridEmailBuilder builder = new SendGridEmailBuilder().from(SENDER_EMAIL).subject(EXAMPLE_SUBJECT).withHtml(EXAMPLE_HTML)

        when:
            SendGridEmail email = builder.to(RECIPIENT_EMAIL).build()
            Map emailParameters = email.toMap()

        then:
            email.to == emailParameters.to == [RECIPIENT_EMAIL]

        when:
            email = new SendGridEmailBuilder().from(SENDER_EMAIL).subject(EXAMPLE_SUBJECT).withHtml(EXAMPLE_HTML).to(RECIPIENT_NAME, RECIPIENT_EMAIL).build()
            emailParameters = email.toMap()

        then:
            email.to == emailParameters.to == [RECIPIENT_EMAIL]
            email.toName == emailParameters.toname == [RECIPIENT_NAME]

    }

    def 'Builder can take multiple recipients'() {

        given:
            SendGridEmailBuilder builder = new SendGridEmailBuilder().from(SENDER_EMAIL).subject(EXAMPLE_SUBJECT).withHtml(EXAMPLE_HTML)

        when:
            SendGridEmail email = builder.to(RECIPIENT_EMAIL).addRecipient(ADDITIONAL_RECIPIENT_EMAIL).build()
            Map emailParameters = email.toMap()

        then:
            email.to == [RECIPIENT_EMAIL, ADDITIONAL_RECIPIENT_EMAIL]
            emailParameters.to == [RECIPIENT_EMAIL, ADDITIONAL_RECIPIENT_EMAIL]
            !emailParameters.containsKey('toname')

        when:
            email = new SendGridEmailBuilder().from(SENDER_EMAIL).subject(EXAMPLE_SUBJECT).withHtml(EXAMPLE_HTML)
                    .to(RECIPIENT_NAME, RECIPIENT_EMAIL).addRecipient(ADDITIONAL_RECIPIENT_NAME, ADDITIONAL_RECIPIENT_EMAIL).build()
            emailParameters = email.toMap()

        then:
            email.to == [RECIPIENT_EMAIL, ADDITIONAL_RECIPIENT_EMAIL]
            emailParameters.to == [RECIPIENT_EMAIL, ADDITIONAL_RECIPIENT_EMAIL]
            email.toName == [RECIPIENT_NAME, ADDITIONAL_RECIPIENT_NAME]
            emailParameters.toname == [RECIPIENT_NAME, ADDITIONAL_RECIPIENT_NAME]

    }

    // TODO: Not enough names for recipients

    def 'Builder can take a sender with a display name'() {

        given:
            SendGridEmail email = createBuilder().withHtml(EXAMPLE_HTML).build()

        when:
            Map emailParameters = email.toMap()

        then:
            email.from == SENDER_EMAIL
            emailParameters.from == SENDER_EMAIL

        when:
            email = new SendGridEmailBuilder().from(SENDER_NAME, SENDER_EMAIL).to(RECIPIENT_EMAIL).subject(EXAMPLE_SUBJECT).withHtml(EXAMPLE_HTML).build()
            emailParameters = email.toMap()

        then:
            email.from == SENDER_EMAIL
            email.fromName == SENDER_NAME
            emailParameters.from == SENDER_EMAIL
            emailParameters.fromname == SENDER_NAME

    }

    // TODO: BCC is an array? Explain!
    def 'Builder can add Bccs'() {

        given:
            SendGridEmail email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).build()

        expect:
            email.bcc == [BCC_RECIPIENT]
            email.toMap().bcc == [BCC_RECIPIENT]
        
        when:
            email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).addBcc(ANOTHER_BCC_RECIPIENT).build()

        then:
            email.bcc == [BCC_RECIPIENT, ANOTHER_BCC_RECIPIENT]
            email.toMap().bcc == [BCC_RECIPIENT, ANOTHER_BCC_RECIPIENT]

    }

    def 'Builder correctly translates optional parameters'() {

        given:
            Date sentDate = new Date()
            SendGridEmail email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addBcc(BCC_RECIPIENT).replyTo(REPLY_TO_RECIPIENT).sentDate(sentDate).build()
            Map emailParameters = email.toMap()

        expect:
            email.bcc == [BCC_RECIPIENT]
            email.replyTo == REPLY_TO_RECIPIENT
            email.sentDate == sentDate

        and:
            emailParameters.bcc == [BCC_RECIPIENT]
            emailParameters.replyto == REPLY_TO_RECIPIENT
            emailParameters.date == sentDate.format("yyyy-MM-dd'T'HH:mm:ssz")

    }

    def 'Builder can add Headers'() {

        given:
            SendGridEmail email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addHeader('my-header', 'my-value').build()

        expect:
            email.headers == ['my-header': 'my-value']
            email.toMap().headers == (email.headers as JSON).toString()

        when:
            email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addHeader('a', '1').addHeader('b', '2').build()

        then:
            email.headers == [a:'1', b:'2']
            email.toMap().headers == (email.headers as JSON).toString()

    }

    def 'Builder can add Custom handling instructions'() {

        given:
            SendGridEmail email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addCustomHandlingInstruction('my-header', 'my-value').build()

        expect:
            email.customHandlingInstructions == ['my-header': 'my-value']
            email.toMap().'x-smtpapi' == (email.customHandlingInstructions as JSON).toString()

        when:
            email = createBuilder().withText(DEFAULT_TEXT_CONTENT).addCustomHandlingInstruction('a', '1').addCustomHandlingInstruction('b', '2').build()

        then:
            email.customHandlingInstructions == [a:'1', b:'2']
            email.toMap().'x-smtpapi' == (email.customHandlingInstructions as JSON).toString()

    }

    def 'Builder can add attachments'() {

        given:
            File file = loadFile('true.png')
            SendGridEmailBuilder emailBuilder = createBuilder().withText(DEFAULT_TEXT_CONTENT).addAttachment(file)

        expect:
            SendGridEmail email = emailBuilder.build()
            email.attachments.size() == 1
            email.toMap().'files[true.png]' == URLEncoder.encode(file.text)

        when: 'a second file is added'
            File file2 = loadFile('false.png')
            emailBuilder.addAttachment(file2)
            email = emailBuilder.build()

        then:
            email.attachments.size() == 2
            email.toMap().keySet().containsAll('files[true.png]', 'files[true.png]')

    }

    private File loadFile(String fileName) {

        URI uri = getClass().getClassLoader().getResource(fileName).toURI()
        return new File(uri)

    }

}

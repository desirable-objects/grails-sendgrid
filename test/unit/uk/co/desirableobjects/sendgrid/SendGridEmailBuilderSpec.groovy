package uk.co.desirableobjects.sendgrid

import spock.lang.Unroll
import org.spockframework.compiler.model.Spec
import spock.lang.Specification
import uk.co.desirableobjects.sendgrid.exception.InvalidEmailException


class SendGridEmailBuilderSpec extends Specification {

    def "Builder can build emails"() {

        given:
            SendGridEmail email = SendGridEmailBuilder.from('antony@example.com').to('antony@example.net').subject('Hello There').withText("What's up?").build()

        expect:
            email.sender == 'antony@example.com'
            email.recipient == 'antony@example.net'
            email.subject == 'Hello There'
            email.text == "What's up?"

        when:
            email = SendGridEmailBuilder.from('antony@example.com').to('antony@example.net').subject('Hello There').withHtml("<h1>What's up?</h1>").build()

        then:
            email.sender == 'antony@example.com'
            email.recipient == 'antony@example.net'
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
                    SendGridEmailBuilder.from('antony@example.com').to('antony@example.net').subject('Hello There'),
                    SendGridEmailBuilder.from('antony@example.com').to('antony@example.net').withText("What's up?"),
                    SendGridEmailBuilder.from('antony@example.com').subject('Hello There').withText("What's up?")
            ]

    }

    def 'Builder can take a recipient with a display name'() {

        given:
            SendGridEmailBuilder builder = SendGridEmailBuilder.from('antony@example.com').subject('Hello There').withHtml("<h1>What's up?</h1>")

        when:
            builder.to('antony@example.net')

        then:
            SendGridEmail email = builder.build()
            email.recipient == 'antony@example.net'
            email.toName == 'antony@example.net'
            Map emailParameters = email.toMap()
            emailParameters.recipient ==

    }


}

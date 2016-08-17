# grails-sendgrid

[![Slack Signup](http://slack-signup.grails.org/badge.svg)](http://slack-signup.grails.org)
[![Travis CI](https://travis-ci.org/desirable-objects/grails-sendgrid.svg)](https://travis-ci.org/desirable-objects/grails-sendgrid)

Introduction
----------

The Grails SendGrid plugin allows you to use the services offered by [SendGrid|http://sendgrid.com] to send email from your application.

Installation
----------

Add the following dependencies in `build.gradle`
```
dependencies {
...
    compile 'desirableobjects.grails.plugins:grails-sendgrid:2.0'
...
}
```

Configuration
----------

Configuration takes place in your application's yaml file.

The basic configuration you will need to use the plugin is:

```yaml
    sendgrid:
        username: 'your-username'
        password: 'your-password'
```

Where your-username and your-password should be replaced with your sendgrid login details.

If you need to override the sengrid API endpoint (such as for development/integration environments, to replace it with a fake 'fixture'), you can do that in the same place:


```yaml
    sendgrid:
        api:
            url: 'http://localhost:8080/your-application/fixture/'
        username: 'your-username'
        password: 'your-password'
```

Note that your @fixture@ controller must have the 'mail.send.json' action configured, and sending and receiving @application/json@ content, as this is what the plugin expects to call.

Sending Email
----------

In a pinch, you can send email using the SendGridService in one of three ways:

* Using any controller's built-in sendMail method, and passing your email details to it.

 > Note: This is the grails-mail way of doing things, and should always be your preferred method to allow for quick and easy interchange of any grails-supported mail plugin.

```groovy
sendMail {
     from 'antony@example.com'
     to 'aiten@example.net'
     to 'wirah@example.org'
     bcc 'yourbcc@example.com'
     subject 'This is the subject line'
     body 'This is our message body'
}
```

* Using the sendMail closure from any class which has a reference to the SendGridService.

As above, but referencing the sendMail method of the SendGridService directly:

```groovy
sendGridService.sendMail {
    ...
}
```

3* Using the email builder

This is useful when you might want a more programmatic approach to sending email.

```groovy
SendGridEmail email = new SendGridEmailBuilder()
                        .from('antony@example.com')
                        .to('aiten@example.net')
                        .subject('This is the subject line')
                        .withText('This is our message body')
                        .build()
```

When you've built your email, pass it to the SendGridService's send method:

```groovy
sendGridService.send(email)
```

The email builder is written as a natural-language type DSL, so you might find that there is more than one way to build your email, but under the covers, they are exactly the same.

For further details, see the sendgrid api [http://docs.sendgrid.com/documentation/api/web-api/mail/]



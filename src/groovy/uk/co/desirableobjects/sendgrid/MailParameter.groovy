package uk.co.desirableobjects.sendgrid

import grails.converters.JSON

enum MailParameter {

    to          ('to',           true,  String),
    toName      ('toname',       false, String),
    smtpApi     ('x-smtpapi',    false, JSON),
    subject     ('subject',      true,  String),
    text        ('text',         false, String),
    html        ('html',         false, String),
    from        ('from',         true,  String),
    bcc         ('bcc',          false, String),
    fromName    ('fromname',     false, String),
    replyTo     ('replyto',      false, String),
    date        ('date',         false, Date),
    files       ('files',        false, File[]),
    headers     ('headers',      false, JSON)

    String parameter
    boolean required
    Class type

    private MailParameter(String parameter, boolean required, Class type) {
        this.parameter = parameter
        this.required = required
        this.type = type
    }

}

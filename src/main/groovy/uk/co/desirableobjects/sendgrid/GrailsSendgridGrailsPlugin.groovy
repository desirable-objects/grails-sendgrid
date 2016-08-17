package uk.co.desirableobjects.sendgrid

import grails.plugins.*

class GrailsSendgridGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0 > *"

    def loadAfter = ["mail"]

    def title = "Grails SendGrid Plugin"
    def author = "Antony Jones"
    def authorEmail = "aj+sendgrid@desirableobjects.co.uk"
    def description = "Allows the sending of Email via SendGrid's services"

    def profiles = ['web']

    def documentation = "https://github.com/desirable-objects/grails-sendgrid"
    def license = "APACHE"
    def issueManagement = [ system: "Github", url: "https://github.com/desirable-objects/grails-sendgrid/issues" ]
    def scm = [ url: 'https://github.com/desirable-objects/grails-sendgrid' ]
    def developers = [
            [name: 'Antony Jones', email: 'aj+sendgrid@desirableobjects.co.uk'],
            [name: 'Roberto Perez', email: 'roberto@perezalcolea.info'],
    ]
}

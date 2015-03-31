class SendgridGrailsPlugin {

    def version = "1.3"
    def grailsVersion = "1.3.1 > *"
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def loadAfter = ["mail"]
    def author = "Antony Jones"
    def authorEmail = "aj+sendgrid@desirableobjects.co.uk"
    def title = "Grails SendGrid Plugin"
    def description = "Allows the sending of Email via SendGrid's services"
    def license = "APACHE"
    def organization = [name: 'Desirable Objects', url: 'http://desirableobjects.co.uk']
    def developers = [
            [name: 'Antony Jones', email: 'aj+sendgrid@desirableobjects.co.uk']
    ]
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPSENDGRID" ]
    def scm = [ url: 'https://github.com/aiten/grails-sendgrid' ]
    def documentation = 'http://aiten.github.com/grails-sendgrid/'

    def doWithSpring = {

        springConfig.addAlias('mailService', 'sendGridService')

    }

}

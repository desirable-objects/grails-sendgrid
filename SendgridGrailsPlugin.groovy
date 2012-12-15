class SendgridGrailsPlugin {

    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def loadAfter = ["mail"]

    def author = "Antony Jones"
    def authorEmail = "aj.desirableobjects.co.uk"
    def title = "Grails SendGrid plugin"
    def description = "Allows the sending of Email via SendGrid's services"

    def documentation = "http://grails.org/plugin/grails-sendgrid"

    def doWithSpring = {

        springConfig.addAlias('mailService', 'sendGridService')

    }

}

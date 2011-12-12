grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits("global") {

    }
    log "warn"
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal(null)
        mavenCentral()
    }

    dependencies {
        runtime 'org.codehaus.groovy.modules.http-builder:http-builder:0.5.1', {
            excludes 'xml-apis'
        }
        test 'org.gmock:gmock:0.8.1'
    }

    plugins {

        test ':spock:0.5-groovy-1.7', {
            export = false
        }
        
        build ':release:1.0.0.RC3', {
            export = false
        }

    }
}

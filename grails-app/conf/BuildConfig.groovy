grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.release.scm.enabled = false

grails.project.dependency.resolution = {
    inherits("global") {

    }

    log "warn"

    repositories {
        grailsHome()
        grailsPlugins()
        grailsCentral()

        mavenLocal
        mavenCentral()
        mavenRepo 'http://repo.desirableobjects.co.uk'
    }

    dependencies {
        runtime 'com.github.groovy-wslite:groovy-wslite:0.8.0'
        compile 'commons-codec:commons-codec:1.7'

    }

    plugins {

        test ':spock:0.7'
        
        build ':release:2.2.0', {
            export = false
        }

    }
}

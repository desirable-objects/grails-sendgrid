
withConfig(configuration) {
    inline(phase: 'CONVERSION') { source, context, classNode ->
        classNode.putNodeMetaData('projectVersion', '2.0')
        classNode.putNodeMetaData('projectName', 'grails-sendgrid-grails2')
        classNode.putNodeMetaData('isPlugin', 'true')
    }
}

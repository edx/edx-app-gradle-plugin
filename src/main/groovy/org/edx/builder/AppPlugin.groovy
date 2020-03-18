package org.edx.builder

import org.gradle.api.*

class AppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("edx", Configuration)
        loadPluginConfiguration(project)
        addTasks(project)
    }
    
    def addTasks(project) {
        def helper = new TaskHelper()
        project.task('printConfigPath') {
            description = "Print the path to the configuration as determined by the edx.properties file"
        } doLast {
            println "Your current configuration path is " + project.edx.dir
        }

        project.task('printConfigFiles') {
            description = "Print the files that will combine into the current configuration"
        } doLast {
            helper.printConfigFiles(project)
        }

        project.task('printConfig') {
            description = "Print the current configuration"
        } doLast {
            helper.printConfig(project)
        }

        project.task('format') {
            description = "Formats the source according to the project standard"
        } doLast {
            def files = project.edx.activeConfig.getSrcFiles(project)
            helper.format(project, files)
        }
    }
    private def loadPluginConfiguration(project) {
        // first find out where the more specific properties are
        try {
            def configFile = project.file('edx.properties')
            if (configFile.exists()) {
                project.apply from: configFile
            }
        }
        catch(GradleException e) {
            println "Could not load edx.properties, using default configuration"
        }

        // then actually load them
        def configPath = project.uri(project.edx.dir + '/' + 'edx.properties')
        try {
            project.apply from: configPath
        }
        catch(GradleException e) {
            println "Configuration in " + configPath + " is malformed."
            throw e
        }
    }
}

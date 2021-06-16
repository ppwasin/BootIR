package com.boot.gradle.template

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(ComponentRegistrar::class)
class TemplateComponentRegistrar(
    private val isEnable: Boolean
) : ComponentRegistrar {

    @Suppress("unused") // Used by service loader
    constructor() : this(
        isEnable = false
    )

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        println("registerProjectComponents")
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
//        val string = configuration.get(TemplateCommandLineProcessor.ARG_STRING, defaultString)
//        val file = configuration.get(TemplateCommandLineProcessor.ARG_FILE, defaultFile)
//        IrGenerationExtension.registerExtension(project, TemplateIrGenerationExtension(messageCollector, string, file))
        if(isEnable) {
            IrGenerationExtension.registerExtension(
                project,
                TemplateIrGenerationExtension(messageCollector, "string", "file")
            )
        }
    }
}
package com.boot.gradle.template

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys

@AutoService(ComponentRegistrar::class)
class TemplateComponentRegistrar: ComponentRegistrar {

//    @Suppress("unused") // Used by service loader
//    constructor() : this(
//        isEnable = true
//    )

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val isPluginEnable = configuration[TemplateCommandLineProcessor.ARG_ENABLE] ?: false
        val isIREnable =  configuration.getBoolean(JVMConfigurationKeys.IR)
        if(isPluginEnable && isIREnable) {
            IrGenerationExtension.registerExtension(
                project,
                TemplateIrGenerationExtension(messageCollector)
            )
        }
    }
}
package com.boot.gradle.template

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class)
class TemplateCommandLineProcessor : CommandLineProcessor {

    companion object {
        //Update string value "enable" here will need to update TemplateIrGradlePlugin/SubpluginOption too
        private const val OPTION_ENABLE = "enable"
        val ARG_ENABLE = CompilerConfigurationKey<Boolean>(OPTION_ENABLE)
    }

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_ENABLE,
            valueDescription = "<true | false>",
            description = "",
            required = true,
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            OPTION_ENABLE -> configuration.put(ARG_ENABLE, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
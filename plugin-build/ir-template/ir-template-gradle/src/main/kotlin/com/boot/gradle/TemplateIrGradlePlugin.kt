package com.boot.gradle

import com.boot.gradle.template.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class TemplateIrGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project): Unit = with(target) {
        extensions.create("templateir", TemplateIrGradlePluginExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    //    override fun getCompilerPluginId(): String = "template-ir-compiler"
    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        kotlinCompilation.dependencies {
            implementation("${BuildConfig.ANNOTATION_LIBRARY_GROUP}:${BuildConfig.ANNOTATION_LIBRARY_NAME}:${BuildConfig.ANNOTATION_LIBRARY_VERSION}")
        }

        val extension = project.extensions.getByType(TemplateIrGradlePluginExtension::class.java)
        val enabled = extension.isEnable.get()

        return project.provider {
            listOf(
                SubpluginOption(key = "enable", value = enabled.toString())
            )
        }
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
            artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
            version = BuildConfig.KOTLIN_PLUGIN_VERSION
        ).also {
            println(
                "[TemplateIrGradlePlugin] " +
                    "groupId: ${it.groupId}, " +
                    "artifactId: ${it.artifactId}, " +
                    "version: ${it.version}"
            )
        }
    }

}

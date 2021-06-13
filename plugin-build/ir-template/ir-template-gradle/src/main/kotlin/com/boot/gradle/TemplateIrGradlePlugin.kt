package com.boot.gradle

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

    override fun getCompilerPluginId(): String = "template-ir-compiler"

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(TemplateIrGradlePluginExtension::class.java)
        val string = extension.stringProperty.get()
        println("stringProperty: $string")
        return project.provider {
            listOf(
                SubpluginOption(key = "string", value = string),
//                SubpluginOption(key = "file", value = extension.fileProperty.get().asFile.path)
            )
        }
    }



        override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
            groupId = "com.boot.ir",
            artifactId = "template-ir-compiler",
            version = "1.0.0"
        )



}

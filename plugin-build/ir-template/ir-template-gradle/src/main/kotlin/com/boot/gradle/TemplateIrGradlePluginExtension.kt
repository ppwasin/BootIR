package com.boot.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class TemplateIrGradlePluginExtension(objects: ObjectFactory) {
    val isEnable: Property<Boolean> = objects.property(Boolean::class.java)
}
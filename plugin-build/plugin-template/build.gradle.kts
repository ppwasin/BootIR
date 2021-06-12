plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("template-gradle-plugin") {
            id = "com.boot.gradle.template-gradle-plugin"
            implementationClass = "com.boot.gradle.TemplateGradlePlugin"
        }
    }
}
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
        create("template-ir-plugin") {
            id = "com.boot.gradle.template-ir-plugin"
            implementationClass = "com.boot.gradle.TemplateIrGradlePlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
}
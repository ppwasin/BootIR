buildscript {
    //    extra["kotlin_plugin_id"] = "com.bnorm.template.kotlin-ir-plugin"
    extra["kotlin_plugin_id"] = "com.boot.gradle.template.ir-template"
}
plugins {
    id("com.github.gmazzo.buildconfig") version "3.0.0" apply false
}

allprojects {
    group = "com.boot.gradle.template"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
    }
}
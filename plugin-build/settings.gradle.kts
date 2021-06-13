pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = ("com.boot.gradle")

include(":plugin-template")
include(":ir-template:ir-template-compiler")
include(":ir-template:ir-template-gradle")
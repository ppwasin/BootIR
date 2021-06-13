pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "plugin-build"

include(":plugin-template")
include(":ir-template:ir-template-compiler")
include(":ir-template:ir-template-gradle")
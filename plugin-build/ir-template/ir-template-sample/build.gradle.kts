plugins {
    kotlin("jvm") version "1.5.10"
    id("com.boot.gradle.template-ir-plugin")
}

repositories {
    mavenCentral()
}


templateir {
    stringProperty.set("Test value")
}

dependencies {
    testImplementation(kotlin("test-junit"))
}
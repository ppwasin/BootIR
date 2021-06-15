plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("java-gradle-plugin") //version specify on parent
    id("com.github.gmazzo.buildconfig") //version specify on parent
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("template-ir-plugin") {
//            id = "com.boot.gradle.template-ir-plugin"
            id = parent?.extra?.get("kotlin_plugin_id") as String
            implementationClass = "com.boot.gradle.TemplateIrGradlePlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
}

/** Specify on parent or compiler module (ir-template-compiler)
 *  build.gradle.kts
    allprojects {
        group = "com.boot.gradle"
        version = "1.0.0"
    }
* */
buildConfig {
    packageName(group.toString())

    val project = project(":ir-template:ir-template-compiler") //kotlin-ir-plugin
    assignString("KOTLIN_PLUGIN_ID", parent?.extra?.get("kotlin_plugin_id"))
    assignString("KOTLIN_PLUGIN_GROUP", project.group)
    assignString("KOTLIN_PLUGIN_NAME", project.name)
    assignString("KOTLIN_PLUGIN_VERSION", project.version)

    val annotationProject = project(":ir-template:ir-template-annotation")
    assignString("ANNOTATION_LIBRARY_GROUP", annotationProject.group)
    assignString("ANNOTATION_LIBRARY_NAME", annotationProject.name)
    assignString("ANNOTATION_LIBRARY_VERSION", annotationProject.version)
}

fun com.github.gmazzo.gradle.plugins.BuildConfigExtension.assignString(key: String, value: Any?) {
    println("$key: $value")
    buildConfigField("String", key, "\"${value}\"")
}
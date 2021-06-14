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
    val id = parent?.extra?.get("kotlin_plugin_id").also { println("id: $it") }
    val group = project.group.also { println("group: $it") }
    val name = project.name.also { println("name: $it") }
    val version = project.version.also { println("version: $it") }

    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"$id\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"$group\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"$name\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"$version\"")

    val annotationProject = project(":ir-template:ir-template-annotation")
    buildConfigField("String", "ANNOTATION_LIBRARY_GROUP", "\"${annotationProject.group}\"")
    buildConfigField("String", "ANNOTATION_LIBRARY_NAME", "\"${annotationProject.name}\"")
    buildConfigField("String", "ANNOTATION_LIBRARY_VERSION", "\"${annotationProject.version}\"")
}

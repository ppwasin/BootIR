dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "boot-ir"
include(":app")
includeBuild("plugin-build")
//includeBuild("plugin-build"){
//    dependencySubstitution {
//        substitute(module("com.boot.gradle:template-ir-plugin"))
//            .with(project(":ir-template:ir-template-compiler"))
//        substitute(module("com.boot.ir:ir-template-gradle"))
//            .with(project(":plugin-build:ir-template:ir-template-gradle"))
//    }
//}
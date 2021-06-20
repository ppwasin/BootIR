# BootIR
Boot strap for Kotlin Backend IR compiler.
IR compiler, "DebugLog", follow step in 
- [IR compiler](https://www.notion.so/Tutorial-798bf5e9ff7440e2b0632d1c40d4e825#bd77e4d644974aa7a02398fcfff4e12e)
- [Gradle plugin composite module] https://www.notion.so/Gradle-plugin-f13f98de32a74e64b5fb55e62908dd55#f0395554ecf9474492cb376804648b08

Project setup
- Composite module
- Nested structure of plugin module (plugin-build > plugin-template, ir-template)


# Project structure
- app module: sample app, shouw usage of IR
- plugin-build: contain plugin modules
  - plugin-template: gradle plugin example
  - ir-template: kotlin ir example

## app
- show example usage of plugins both (IR, gradle plugin)
- see app/build.gradle.kts: id("com.boot.gradle.template.ir-template")

## plugin-build
- settings.gradle.kts: define its submodules
- build.gradle.kts: define version of infrasturcture libs (such as kotlin). So the submodule doesn't need to define version

## plugin-template
- use plugin `java-gradle-plugin`
- define gradle plugin id, implementationsClass with gradlePlugin

## plugin-build/ir-template
- use subproject to identify group, version [ir-template/build.gradle.kts](https://github.com/wasinpp/BootIR/blob/main/plugin-build/ir-template/build.gradle.kts)
- ir-template-gradle: expose backend IR compiler as the gradle plugin
- ir-template-compiler: actual backend IR compiler
- ir-template-annotation: @DebugLog annotation, this may need or not needed depend on the requirements of IR compiler

# TODO
- Finish ir-template-gradle-native: IR on native platform

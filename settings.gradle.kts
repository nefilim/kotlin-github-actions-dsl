enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "kotlin-github-actions-dsl"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
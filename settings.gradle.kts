enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "kotlin-github-actions-dsl"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

rootDir.listFiles()?.filter {
    File(it, "build.gradle.kts").exists() && !it.name.contains("build")
}?.forEach {
    println("including project [${it.name}]")
    include(it.name)
}
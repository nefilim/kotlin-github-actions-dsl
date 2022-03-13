plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-conventions")
    id("build-conventions")
    id("publishing-conventions")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    listOf(
        projects.core,
        libs.kotlin.reflect,
        libs.kotlinx.serialization.yaml,
        libs.kotlinLogging,
        libs.kotlinPoet,
    ).map {
        implementation(it)
    }
}

tasks.register<JavaExec>("generateActions") {
    mainClass.set("io.github.nefilim.githubactions.generator.GitHubActionGeneratorKt")
    classpath = sourceSets.main.get().runtimeClasspath
    args(project(":actions").layout.projectDirectory.dir("src/generated/kotlin").toString())
    finalizedBy()
}

tasks.getByName("assemble") {
    dependsOn("generateActions")
}
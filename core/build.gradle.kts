import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.tasktree)
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
        libs.kotlin.reflect,
        libs.kotlinx.coroutines.core,
        libs.kotlinx.serialization.yaml,
        libs.kotlinLogging,
        libs.kotlinPoet,
    ).map {
        implementation(it)
    }

    listOf(
        libs.kotest.runner,
        libs.kotest.assertions.core,
    ).map {
        testImplementation(it)
    }
}
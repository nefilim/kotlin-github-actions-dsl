plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    id("kotlin-conventions")
    id("build-conventions")
    id("publishing-conventions")
}

dependencies {
    listOf(
        projects.core,
        projects.actionGenerator,
        libs.kotlin.reflect,
        libs.kotlinx.serialization.yaml,
        libs.kotlinLogging,
        libs.kotlinPoet,
    ).map {
        implementation(it)
    }
}

sourceSets {
    main {
        java.srcDir(layout.projectDirectory.dir("src/generated/kotlin"))
    }
}

tasks.create("cleanGeneratedActions") {
    project.delete(layout.projectDirectory.dir("src/generated"))
}
tasks.getByName("clean") {
    dependsOn("cleanGeneratedActions")
}

ktlint {
    version.set(libs.versions.ktlint)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(libs.versions.ktlint)
    debug.set(false)
    verbose.set(false)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    disabledRules.set(setOf("final-newline", "experimental:trailing-comma"))
    baseline.set(file("my-project-ktlint-baseline.xml"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }
    filter {
        include("**/generated/kotlin/**")
    }
}

tasks.getByName("compileKotlin") {
    dependsOn("ktlintFormat")
}
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-conventions")
    id("build-conventions")
    id("publishing-conventions")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
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

ktlint {
    version.set("0.44.0")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("0.44.0")
    debug.set(true)
    verbose.set(true)
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
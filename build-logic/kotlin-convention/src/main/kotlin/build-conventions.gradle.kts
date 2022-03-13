plugins {
    id("com.adarshr.test-logger")
}

group = "io.github.nefilim.githubactions"

configure<com.adarshr.gradle.testlogger.TestLoggerExtension> {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
    showCauses = true
    slowThreshold = 1000
    showSummary = true
    showStandardStreams = true
}


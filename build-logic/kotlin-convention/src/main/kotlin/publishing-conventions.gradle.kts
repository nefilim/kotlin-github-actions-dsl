plugins {
    `maven-publish`
    signing
}

fun Project.repoURL(): String {
    return if (version.toString().endsWith("SNAPSHOT"))
        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    else
        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

signing {
    val skipSigning = findProperty("skipSigning")?.let { (it as String).toBoolean() } ?: false
    if (!skipSigning) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    } else {
        logger.warn("skipping signing")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("kotlin-github-actions-dsl")
                description.set("A Kotlin DSL for generating Github Actions in YAML")
                url.set("https://github.com/nefilim/ghakdsl")
                licenses {
                    license {
                        name.set("GPL-3.0-only")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }
                developers {
                    developer {
                        id.set("nefilim")
                        name.set("nefilim")
                        email.set("nefilim@hotmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/nefilim/ghakdsl.git")
                    url.set("https://github.com/nefilim/ghakdsl")
                }
            }
            artifactId = "kotlin-dsl"
            groupId = project.group.toString()
            version = project.version.toString()
            from(components["java"])
        }
    }
}
import com.google.gradle.osdetector.OsDetector

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("com.google.osdetector")
    id("com.github.jmongard.git-semver-plugin")
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/MDL-Viewer/hott-ui")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

val os = osdetector.os
val os_platform = when (os) {
    "osx" -> "mac"
    "windows" -> "win"
    "linux" -> "linux"
    else -> throw UnsupportedOperationException("os $os is not supported")
}

dependencies {
    api("org.openjfx:javafx-base:_")
    api("org.openjfx:javafx-base:_:$os_platform")
    api("org.openjfx:javafx-graphics:_")
    api("org.openjfx:javafx-graphics:_:$os_platform")
    api("org.openjfx:javafx-controls:_")
    api("org.openjfx:javafx-controls:_:$os_platform")
    api("org.openjfx:javafx-web:_")
    api("org.openjfx:javafx-web:_:$os_platform")
    api("org.openjfx:javafx-media:_")
    api("org.openjfx:javafx-media:_:$os_platform")
    api("no.tornado:tornadofx:_")
    api("de.treichels.hott:hott-util:_")
}

semver {
    releaseTagNameFormat = "v%s"
}

version = semver.version

tasks {
    jar {
        manifest {
            attributes (
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
            )
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MDL-Viewer/hott-ui")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("github") {
            artifactId = project.name.lowercase()
            group = "de.treichels.hott"
            from(components["java"])
        }          
    }
}

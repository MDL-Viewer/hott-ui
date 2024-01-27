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

val osPlatform = when (val os = osdetector.os) {
    "osx" -> "mac"
    "windows" -> "win"
    "linux" -> "linux"
    else -> throw UnsupportedOperationException("os $os is not supported")
}

dependencies {
    implementation("org.openjfx:javafx-base:_")
    implementation("org.openjfx:javafx-base:_:$osPlatform")
    implementation("org.openjfx:javafx-graphics:_")
    implementation("org.openjfx:javafx-graphics:_:$osPlatform")
    implementation("org.openjfx:javafx-controls:_")
    implementation("org.openjfx:javafx-controls:_:$osPlatform")
    implementation("org.openjfx:javafx-web:_")
    implementation("org.openjfx:javafx-web:_:$osPlatform")
    implementation("no.tornado:tornadofx:_")
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

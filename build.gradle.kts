plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("com.github.jmongard.git-semver-plugin")
    id("org.openjfx.javafxplugin")
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

javafx {
    modules = listOf("javafx.base", "javafx.graphics", "javafx.controls", "javafx.web")
}

dependencies {
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

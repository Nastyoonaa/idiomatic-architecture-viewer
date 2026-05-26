plugins {
    kotlin("jvm") version "2.0.20"
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"

    id("com.vanniktech.maven.publish") version "0.34.0"
    kotlin("plugin.serialization") version "2.0.20"

}

group = "io.github.nastyoonaa"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.sourceforge.plantuml:plantuml:1.2024.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.json:json:20240303")
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(17)
}

mavenPublishing {
    configure(
        com.vanniktech.maven.publish.KotlinJvm()
    )

    publishToMavenCentral()

    signAllPublications()

    coordinates(
        "io.github.nastyoonaa",
        "idiomatic-architecture-viewer",
        "0.1.0"
    )

    pom {
        name.set("Idiomatic Architecture Viewer")
        description.set("Interactive architecture viewer and UML analyzer for Kotlin projects using KSP")
        url.set("https://github.com/Nastyoonaa/idiomatic-architecture-viewer")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("nastyoonaa")
                name.set("Анастасия Ципенюк")
            }
        }

        scm {
            url.set(
                "https://github.com/Nastyoonaa/idiomatic-architecture-viewer"
            )
        }
    }
}
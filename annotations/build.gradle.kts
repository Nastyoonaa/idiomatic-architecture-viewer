plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.nastyoonaa"
version = property("libraryVersion").toString()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    jvm()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
}

mavenPublishing {
    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform()
    )

    publishToMavenCentral()

    signAllPublications()

    coordinates(
        "io.github.nastyoonaa",
        "idiomatic-architecture-viewer-annotations",
        version.toString()
    )

    pom {
        name.set("Idiomatic Architecture Viewer Annotations")

        description.set(
            "Multiplatform annotations and generated model API for Idiomatic Architecture Viewer"
        )

        url.set(
            "https://github.com/Nastyoonaa/idiomatic-architecture-viewer"
        )

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
            connection.set(
                "scm:git:git://github.com/Nastyoonaa/idiomatic-architecture-viewer.git"
            )

            developerConnection.set(
                "scm:git:ssh://github.com/Nastyoonaa/idiomatic-architecture-viewer.git"
            )

            url.set(
                "https://github.com/Nastyoonaa/idiomatic-architecture-viewer"
            )
        }
    }
}

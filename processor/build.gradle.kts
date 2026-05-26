plugins {
    kotlin("jvm") version "2.0.20"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.nastyoonaa"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.devtools.ksp:symbol-processing-api:2.0.20-1.0.25")
    implementation("net.sourceforge.plantuml:plantuml:1.2024.6")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
mavenPublishing {

    configure(
        com.vanniktech.maven.publish.KotlinJvm()
    )

    publishToMavenCentral()

    signAllPublications()

    coordinates(
        "io.github.nastyoonaa",
        "idiomatic-architecture-viewer-processor",
        "0.1.0"
    )

    pom {

        name.set("Idiomatic Architecture Viewer Processor")

        description.set(
            "KSP processor for architecture analysis and UML generation"
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
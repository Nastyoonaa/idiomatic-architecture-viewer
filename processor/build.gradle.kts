plugins {
    kotlin("jvm") version "2.0.20"

    `java-library`
    `maven-publish`
    signing
}

group = "io.github.nastyoonaa"
version = "0.1.0"

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

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("processor") {

            from(components["java"])

            groupId = "io.github.nastyoonaa"
            artifactId = "idiomatic-architecture-viewer-processor"
            version = "0.1.0"

            pom {
                name.set("Idiomatic Architecture Viewer Processor")
                description.set("KSP processor for architecture analysis and UML generation")
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
                    connection.set("scm:git:git://github.com/Nastyoonaa/idiomatic-architecture-viewer.git")
                    developerConnection.set("scm:git:ssh://github.com/Nastyoonaa/idiomatic-architecture-viewer.git")
                    url.set("https://github.com/Nastyoonaa/idiomatic-architecture-viewer")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["processor"])
}
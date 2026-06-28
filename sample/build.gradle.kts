plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    ksp(project(":processor"))
}

kotlin {
    jvmToolchain(17)
}

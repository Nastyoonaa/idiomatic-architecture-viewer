plugins {
    id("com.android.library") version "8.5.2"
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":annotations"))
            }
        }
    }
}

android {
    namespace = "com.example.kmpsample"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":processor"))
    add("kspAndroid", project(":processor"))
    add("kspIosX64", project(":processor"))
    add("kspIosSimulatorArm64", project(":processor"))
}

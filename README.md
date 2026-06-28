# Idiomatic Architecture Viewer

Interactive architecture analysis and visualization tool for Kotlin projects powered by KSP (Kotlin Symbol Processing).

The project analyzes Kotlin source code during compilation and generates:

- interactive HTML architecture viewer
- package navigation pages
- class dependency pages
- Mermaid dependency graphs
- PlantUML diagrams
- architecture metrics
- dependency cycle reports
- JSON architecture export

The generated reports allow exploring project structure directly in the browser.

---

# Features

## Architecture Analysis

- class dependency analysis
- constructor dependency detection
- method dependency analysis
- package structure analysis
- module detection
- sourceSet detection
- architecture layer detection
- dependency cycle detection

---

## Interactive HTML Viewer

The project generates a full interactive architecture website.

Includes:

- architecture overview page
- package pages
- class pages
- clickable dependency navigation
- Mermaid dependency graphs
- package explorer
- class dependency explorer

---

## Diagram Generation

Supported outputs:

- PlantUML
- Mermaid
- HTML
- JSON
- Markdown reports

---

## Metrics & Reports

The library generates:

- architecture metrics
- class metrics
- dependency cycle reports
- architecture graphs
- module dependency diagrams

---

# Example

## Input

```kotlin
@UmlDiagram
class UserService(
    private val repository: UserRepository
)
```

---

## Generated Dependency Graph

```mermaid
graph TD
UserService --> UserRepository
```

---

## Generated HTML Viewer

The processor automatically generates:

```text
architecture.html
com_example_service.html
UserService.html
```

with interactive navigation between pages.

---

# Generated Output

## Architecture Overview

- project structure visualization
- module overview
- package navigation
- dependency graph

## Package Pages

Each package page contains:

- package classes
- dependency graph
- navigation between classes

## Class Pages

Each class page contains:

- dependencies
- methods
- properties
- Mermaid graph
- clickable dependency links

---

# Architecture

```text
Kotlin Source Code
        │
        ▼
KSP Processor
        │
        ▼
Static Code Analysis
        │
        ├── Dependency Analysis
        ├── Package Analysis
        ├── Module Detection
        ├── Metrics
        └── Cycle Detection
        │
        ▼
Generators
        │
        ├── HTML
        ├── PlantUML
        ├── Mermaid
        ├── JSON
        └── Markdown Reports
        │
        ▼
Generated Architecture Viewer
```

---

# Technologies

- Kotlin
- KSP (Kotlin Symbol Processing)
- Mermaid.js
- PlantUML
- Gradle
- Kotlin Serialization
- OkHttp

---

# Project Structure

```text
idiomatic-architecture-viewer
│
├── processor
│
├── analysis
│   ├── CycleDetector
│   └── ArchitectureAnalysis
│
├── diagram
│   ├── ArchitectureGraphGenerator
│   ├── PackageDiagramGenerator
│   ├── MetricsReportGenerator
│   └── ModuleDiagramGenerator
│
├── export
│   ├── ArchitectureHtmlExporter
│   ├── PackageHtmlExporter
│   ├── ClassHtmlExporter
│   └── ArchitectureJsonExporter
│
├── generation
│   ├── HtmlGenerationService
│   ├── DiagramGenerationService
│   ├── JsonGenerationService
│   └── UmlClassGenerationService
│
├── metrics
│
├── uml
│
├── writer
│
├── build.gradle.kts
└── settings.gradle.kts
```

---

# Installation

The annotation API is intentionally small. In source code you always use the
same annotation:

```kotlin
import uml.UmlDiagram

@UmlDiagram
class UserService
```

The Gradle setup is different for JVM-only and Kotlin Multiplatform projects.
Use the same `VERSION` for all Idiomatic Architecture Viewer artifacts.

## JVM Project

```kotlin
plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(
        "io.github.nastyoonaa:idiomatic-architecture-viewer:VERSION"
    )

    ksp(
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )
}
```

## Kotlin Multiplatform Project

Use the multiplatform annotations artifact from `commonMain`. The KSP processor
still runs only during Gradle compilation.

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(
                "io.github.nastyoonaa:idiomatic-architecture-viewer-annotations:VERSION"
            )
        }
    }
}

dependencies {
    add(
        "kspCommonMainMetadata",
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )

    add(
        "kspAndroid",
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )

    add(
        "kspIosX64",
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )

    add(
        "kspIosArm64",
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )

    add(
        "kspIosSimulatorArm64",
        "io.github.nastyoonaa:idiomatic-architecture-viewer-processor:VERSION"
    )
}
```

For Kotlin Multiplatform, the generated static viewer appears under the KSP
output of each target, for example:

```text
build/generated/ksp/metadata/commonMain/resources/com/example/generated/architecture/architecture.html
build/generated/ksp/android/androidDebug/resources/com/example/generated/architecture/architecture.html
build/generated/ksp/iosX64/iosX64Main/resources/com/example/generated/architecture/architecture.html
build/generated/ksp/iosSimulatorArm64/iosSimulatorArm64Main/resources/com/example/generated/architecture/architecture.html
```

---

# Usage

Annotate classes:

```kotlin
import uml.UmlDiagram

@UmlDiagram
class UserService(
    private val repository: UserRepository
)
```

Build the project:

```bash
./gradlew build
```

Generated files will appear in:

```text
build/generated/ksp/
```

Open `architecture.html` in a browser to inspect the generated static viewer.

---

# Generated Files

Examples:

```text
architecture.html
ArchitectureOverview.puml
ArchitectureMetrics.md
DependencyCycles.md
ArchitectureGraph.puml
com_example_service.html
UserService.html
```

---

# Mermaid Example

```mermaid
graph TD
UserService --> UserRepository
UserRepository --> Database
```

---

# PlantUML Example

```plantuml
@startuml

class UserService
class UserRepository

UserService --> UserRepository

@enduml
```

---

# Roadmap

Planned features:

- IntelliJ IDEA plugin
- Android Studio plugin
- live architecture updates
- architecture diff reports
- CI/CD integration
- architectural rule validation
- graph filtering
- architecture snapshots
- dark mode viewer
- graph clustering
- AI-powered architecture recommendations

---

# Publishing

Artifacts are published to Maven Central.

Published artifacts:

```text
io.github.nastyoonaa:idiomatic-architecture-viewer
io.github.nastyoonaa:idiomatic-architecture-viewer-processor
io.github.nastyoonaa:idiomatic-architecture-viewer-annotations
```

Release CI validates the project first and then publishes all configured
Maven Central publications.

---

# Author

Anastasia Tsipenyuk

GitHub:
https://github.com/Nastyoonaa

Telegram:
@Iydyshka_krovopivyshka

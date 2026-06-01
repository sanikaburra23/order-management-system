plugins {
    java
}

group = "com.b2r"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.dropwizard:dropwizard-core:2.1.6")
    implementation("io.dropwizard:dropwizard-hibernate:2.1.6")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("javax.validation:validation-api:2.0.1.Final")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

// Simple primitive syntax that works on ALL versions of Gradle Kotlin DSL
tasks.jar {
    manifest {
        attributes("Main-Class" to "com.b2r.ordermgmt.OrderManagementApplication")
    }

    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE

    // Grabs dependencies directly using a primitive inline execution loop
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}


import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
}

application {
    mainClass.set("MainKt")
}
val mainClass = "MainKt"
group = "me.ropol"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.ktor:ktor-client-core:2.1.2")
    implementation("io.ktor:ktor-client-cio:2.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xdebug"
    )
}

val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    baseName = "${project.name}-fat"
    manifest {
        attributes["Main-Class"] = mainClass
    }
    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks {
    "jar" {
        dependsOn(fatJar)
    }
}

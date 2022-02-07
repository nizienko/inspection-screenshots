import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/iuia/qa-automation-maven")
}

// https://jetbrains.team/p/iuia/packages/maven/qa-automation-maven/com.intellij.remoterobot/remote-robot
val remoteRobotVersion = "0.11.11.191"

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.intellij.remoterobot:remote-robot:$remoteRobotVersion")
    testImplementation("com.intellij.remoterobot:ide-launcher:$remoteRobotVersion")
    implementation("com.intellij.remoterobot:remote-fixtures:$remoteRobotVersion")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
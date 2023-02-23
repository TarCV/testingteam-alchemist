plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.github.TarCV.testing"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val kodeInVersion = "7.17.0"

    implementation(kotlin("reflect"))

    // 'compileOnly kodein-di' is used only to provide matching version of kaverit in the line after the next,
    // kodein-di is not actually used in the code
    compileOnly("org.kodein.di:kodein-di:$kodeInVersion")
    implementation("org.kodein.type:kaverit")

    testImplementation(kotlin("test"))
    testImplementation("org.kodein.di:kodein-di:$kodeInVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

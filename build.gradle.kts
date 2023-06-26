plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"

    id("net.mamoe.mirai-console") version "2.15.0-RC"
    application
}

group = "sh.xsl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson", "gson", "2.10.1")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", "2.15.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("ByeMiniAppMain")
}
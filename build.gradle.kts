plugins {
    kotlin("jvm") version "1.4.32"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "1.5.4"

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("org.mnode.ical4j:ical4j:3.0.25")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("io.strikt:strikt-core:0.31.0")
}

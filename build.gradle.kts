plugins {
    kotlin("jvm") version "1.4.32"
}

group = "de.nielsfalk.windcal"
version = "0.1-SNAPSHOT"

val logback_version = "1.2.1"
val ktor_version = "1.5.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("org.mnode.ical4j:ical4j:3.0.25")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("io.strikt:strikt-core:0.31.0")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

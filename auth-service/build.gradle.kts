import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.testImplementation

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.netty:netty-transport:4.2.13.Final")

    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("io.netty:netty-handler:4.2.13.Final")

    implementation("org.postgresql:r2dbc-postgresql:1.1.1.RELEASE")
    implementation("io.projectreactor:reactor-core:3.8.5")

    implementation("org.aeonbits.owner:owner:1.0.12")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.auth0:java-jwt:4.5.2")

    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("io.projectreactor:reactor-test:3.8.5")

    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.5")
    testImplementation("org.testcontainers:testcontainers-r2dbc:2.0.5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.5")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.processTestResources {
    doNotTrackState("")
}
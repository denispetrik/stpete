import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    kotlin("plugin.spring") version "1.3.21"
    id("org.springframework.boot") version "2.1.1.RELEASE"
    id("generate-jooq")
}

apply(plugin = "io.spring.dependency-management")

group = "den.ptrq"
version = "0.0.1-SNAPSHOT"

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("$buildDir/generated")
        }
    }
}

generateJooq {
    migrationLocation = "filesystem:src/main/resources/db/migration"
    targetLocation = "$buildDir/generated"
    targetPackage = "den.ptrq.stpete.jooq"
}

tasks {
    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "1.8"
        }
        dependsOn("generateJooq")
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "1.8"
        }
        dependsOn("generateJooq")
    }

    named<Test>("test") {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.httpcomponents:httpclient")
    implementation("org.hsqldb:hsqldb:2.4.1")
    implementation("org.flywaydb:flyway-core:5.2.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

repositories {
    mavenCentral()
}

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Target

plugins {
    kotlin("jvm") version "1.3.21"
    kotlin("plugin.allopen") version "1.3.21"
    kotlin("plugin.spring") version "1.3.21"
    id("org.springframework.boot") version "2.1.3.RELEASE"
}

buildscript {
    dependencies {
        classpath("com.h2database:h2:1.4.197")
        classpath("org.flywaydb:flyway-core:5.2.4")
        classpath("org.jooq:jooq-codegen:3.11.9")
    }
}

apply(plugin = "io.spring.dependency-management")

group = "den.ptrq"
version = "0.0.1-SNAPSHOT"

allOpen {
    annotation("den.ptrq.stpete.MockableInTests")
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated")
        }
    }

    create("intTest") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("/src/intTest/kotlin")
            resources.srcDir("/src/intTest/resources")
        }
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

configurations {
    getByName("intTestCompileClasspath").extendsFrom(getByName("testCompileClasspath"))
    getByName("intTestRuntimeClasspath").extendsFrom(getByName("testRuntimeClasspath"))
}

tasks {
    withType(KotlinCompile::class) {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "1.8"
        }
        dependsOn("generateJooq")
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    create<Test>("intTest") {
        group = "verification"
        description = "Runs all integration tests"

        useJUnitPlatform()
        testClassesDirs = sourceSets["intTest"].output.classesDirs
        classpath = sourceSets["intTest"].runtimeClasspath

        getByName("check").dependsOn(this)
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.httpcomponents:httpclient")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("com.h2database:h2:1.4.197")
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

tasks.create("generateJooq") {
    group = "jooq"
    description = "Generates jooq classes based on migrated in-memory database"

    doLast {
        val jdbcUrl = "jdbc:h2:mem:db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=10"
        val dbUser = "sa"
        val dbPassword = ""
        val schema = "public"

        val migrationLocation = "filesystem:src/main/resources/db/migration"
        val targetLocation = "$buildDir/generated"
        val targetPackage = "den.ptrq.stpete.jooq"

        val classLoader = Thread.currentThread().contextClassLoader
        val dataSource = DriverDataSource(classLoader, "org.h2.Driver", jdbcUrl, dbUser, dbPassword)

        Flyway.configure()
            .dataSource(dataSource)
            .schemas(schema)
            .locations(migrationLocation)
            .load()
            .migrate()

        val database = Database()
            .withName("org.jooq.meta.h2.H2Database")
            .withExcludes("flyway_schema_history")
            .withInputSchema(schema)

        val target = Target()
            .withPackageName(targetPackage)
            .withDirectory(targetLocation)

        val generator = Generator()
            .withDatabase(database)
            .withTarget(target)

        val jdbc = Jdbc()
            .withUrl(jdbcUrl)
            .withUser(dbUser)
            .withPassword(dbPassword)

        GenerationTool.generate(
            Configuration()
                .withGenerator(generator)
                .withJdbc(jdbc)
        )
    }
}

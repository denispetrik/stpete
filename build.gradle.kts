import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Target

plugins {
    kotlin("jvm") version "1.3.21"
    kotlin("plugin.spring") version "1.3.21"
    id("org.springframework.boot") version "2.1.1.RELEASE"
}

buildscript {
    dependencies {
        classpath("org.hsqldb:hsqldb:2.4.1")
        classpath("org.flywaydb:flyway-core:5.2.4")
        classpath("org.jooq:jooq-codegen:3.11.7")
        classpath("org.jooq:jooq-meta:3.11.7")
    }
}

apply(plugin = "io.spring.dependency-management")

group = "den.ptrq"
version = "0.0.1-SNAPSHOT"

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated")
        }
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

tasks {
    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "1.8"
        }
        dependsOn("generateJooqClasses")
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "1.8"
        }
        dependsOn("generateJooqClasses")
    }

    named<Test>("test") {
        useJUnitPlatform()
    }
}

tasks.register<GenerateJooqClasses>("generateJooqClasses") {
    migrationLocation = "filesystem:src/main/resources/db/migration"
    targetLocation = "$buildDir/generated"
    targetPackage = "den.ptrq.stpete.jooq"
}

open class GenerateJooqClasses : DefaultTask() {

    init {
        group = "jooq"
        description = "generates jooq classes based on migrated in-memory database"
    }

    lateinit var migrationLocation: String
    lateinit var targetLocation: String
    lateinit var targetPackage: String

    @TaskAction
    fun action() {
        val dbUser = "sa"
        val dbPassword = ""
        val schema = "p"
        val jdbcUrl = "jdbc:hsqldb:mem:db"

        val classLoader = Thread.currentThread().contextClassLoader
        val properties = mapOf("sql.syntax_pgs" to "true").toProperties()
        val dataSource = DriverDataSource(
            classLoader, "org.hsqldb.jdbc.JDBCDriver", jdbcUrl, dbUser, dbPassword, properties
        )

        try {
            Flyway.configure()
                .dataSource(dataSource)
                .schemas(schema)
                .locations(migrationLocation)
                .load()
                .migrate()

            val database = Database()
                .withName("org.jooq.meta.hsqldb.HSQLDBDatabase")
                .withExcludes("flyway_schema_history")
                .withInputSchema(schema)
            val target = Target().withPackageName(targetPackage).withDirectory(targetLocation)
            val generator = Generator().withDatabase(database).withTarget(target)
            val jdbc = Jdbc().withUrl(jdbcUrl).withUser(dbUser).withPassword(dbPassword)
            val configuration = Configuration().withGenerator(generator).withJdbc(jdbc)
            GenerationTool.generate(configuration)
        } finally {
            dataSource.connection.close()
        }
    }
}

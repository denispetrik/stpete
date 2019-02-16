plugins {
    `embedded-kotlin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("generate-jooq-plugin") {
            id = "generate-jooq"
            implementationClass = "den.ptrq.gradle.plugins.GenerateJooqPlugin"
        }
    }
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation("org.hsqldb:hsqldb:2.4.1")
    implementation("org.flywaydb:flyway-core:5.2.4")
    implementation("org.jooq:jooq-codegen:3.11.7")
    implementation("org.jooq:jooq-meta:3.11.7")
}

repositories {
    jcenter()
}

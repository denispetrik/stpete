package den.ptrq.gradle.plugins

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import javax.sql.DataSource

/**
 * @author petrique
 */

open class GenerateJooqExtension {
    var migrationLocation: String? = null
    var targetPackage: String? = null
    var targetLocation: String? = null
}

class GenerateJooqPlugin : Plugin<Project> {

    private val dbUser = "sa"
    private val dbPassword = ""
    private val schema = "public"
    private val jdbcUrl = "jdbc:hsqldb:mem:db"

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaBasePlugin::class.java)

        val ext = project.extensions.create<GenerateJooqExtension>("generateJooq")

        val generateJooqTask = project.task("generateJooq") {
            group = "jooq"
            description = "generates jooq classes based on migrated in-memory database"

            doLast {
                val dataSource = startInMemoryDatabase()
                try {
                    migrateDatabase(dataSource, ext)
                    generateJooqClasses(ext)
                } finally {
                    stopInMemoryDatabase(dataSource)
                }
            }
        }

        project.tasks
            .filter { it.name == "compileKotlin" || it.name == "compileTestKotlin" }
            .forEach { it.dependsOn += generateJooqTask }

        project.afterEvaluate {
            val sourceSetContainer = project.convention.getByType(SourceSetContainer::class.java)
            sourceSetContainer.getByName("main").java.srcDir(ext.targetLocation!!)
        }
    }

    private fun startInMemoryDatabase(): DataSource {
        val classLoader = Thread.currentThread().contextClassLoader
        val properties = mapOf("sql.syntax_pgs" to "true").toProperties()
        return DriverDataSource(
            classLoader, "org.hsqldb.jdbc.JDBCDriver", jdbcUrl, dbUser, dbPassword, properties
        )
    }

    private fun stopInMemoryDatabase(dataSource: DataSource) = dataSource.connection.close()

    private fun migrateDatabase(dataSource: DataSource, ext: GenerateJooqExtension) =
        Flyway.configure()
            .dataSource(dataSource)
            .schemas(schema)
            .locations(ext.migrationLocation)
            .load()
            .migrate()

    private fun generateJooqClasses(ext: GenerateJooqExtension) =
        GenerationTool.generate(
            Configuration()
                .withGenerator(generator(ext))
                .withJdbc(jdbc())
        )

    private fun generator(ext: GenerateJooqExtension) = Generator()
        .withDatabase(database())
        .withTarget(target(ext))

    private fun database() = Database()
        .withName("org.jooq.meta.hsqldb.HSQLDBDatabase")
        .withExcludes("flyway_schema_history")
        .withInputSchema(schema)

    private fun target(ext: GenerateJooqExtension) = Target()
        .withPackageName(ext.targetPackage)
        .withDirectory(ext.targetLocation)

    private fun jdbc() = Jdbc().withUrl(jdbcUrl).withUser(dbUser).withPassword(dbPassword)
}

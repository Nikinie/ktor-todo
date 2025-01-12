import nu.studer.gradle.jooq.JooqGenerate

val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "1.3.50"
    id("nu.studer.jooq") version "9.0"
    id("io.ktor.plugin") version "3.0.3"
    id("org.flywaydb.flyway") version "11.1.1"
}

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.1.1")
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:11.1.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.1.1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.jooq:jooq:3.19.17")
    jooqGenerator("org.postgresql:postgresql:42.7.4")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    jooqGenerator("org.glassfish.jaxb:jaxb-runtime:4.0.5")
}

val dbUrl = "jdbc:postgresql://localhost:5432/postgres"
val dbUser = "ktor_user"
val dbPassword = "secretpw"

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
    locations = arrayOf("filesystem:src/main/resources/db/migrations")
    createSchemas = true
    schemas = arrayOf("public")
    baselineOnMigrate = true
}

jooq {
    version.set("3.19.17")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"

                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"  // Include all tables
                        excludes = ""    // No exclusions

                    }

                    target.apply {
                        packageName = "com.nikinie.jooq"
                        directory = "build/generated-src/jooq/main"
                    }

                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isPojos = true
                        isDaos = true
                    }
                }
            }
        }
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    inputs.files(fileTree("src/main/resources/db/migrations"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
}

sourceSets {
    main {
        java {
            srcDir("build/generated-src/jooq/main")
        }
    }
}
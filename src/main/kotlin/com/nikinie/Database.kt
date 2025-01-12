package com.nikinie

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.impl.DSL

object DatabaseFactory {
    lateinit var dslContext: DSLContext
        private set

    fun init(environment: ApplicationEnvironment) {
        val config = HikariConfig().apply {
            jdbcUrl = environment.config.property("database.url").getString()
            driverClassName = environment.config.property("database.driver").getString()
            username = environment.config.property("database.user").getString()
            password = environment.config.property("database.password").getString()
        }
        val dataSource = HikariDataSource(config)
        Flyway.configure().dataSource(dataSource).load().migrate()
        dslContext = DSL.using(dataSource, org.jooq.SQLDialect.POSTGRES)
    }
}

fun Application.configureDatabases() {
    DatabaseFactory.init(environment)
}
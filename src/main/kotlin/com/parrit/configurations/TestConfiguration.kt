package com.parrit.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DriverManagerDataSource

@Configuration
@Profile("test")
class TestConfiguration {

    @Bean
    fun dataSource(): DriverManagerDataSource {
        val driverManagerDataSource = DriverManagerDataSource()
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver")
        driverManagerDataSource.url = "jdbc:postgresql://localhost:5432/local_parrit_test"
        return driverManagerDataSource
    }
}

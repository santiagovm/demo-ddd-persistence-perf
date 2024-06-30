package com.example.ddd_demo.persistence

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class DbConfig : AbstractJdbcConfiguration() {

    @Bean
    override fun userConverters(): MutableList<*> {
        return mutableListOf(
            JsonbTypeHandler.TasksToJsonConverter(),
            JsonbTypeHandler.JsonbToTasksConverter(),
        )
    }
}

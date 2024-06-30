package com.example.ddd_demo.persistence

import com.example.ddd_demo.domain.TasksWrapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.integration.support.json.Jackson2JsonObjectMapper

object JsonbTypeHandler {
    private val objectMapper = Jackson2JsonObjectMapper().objectMapper

    @WritingConverter
    class TasksToJsonConverter : Converter<TasksWrapper, PGobject> {
        override fun convert(source: TasksWrapper): PGobject = PGobject().apply {
            type = "jsonb"
            value = objectMapper.writeValueAsString(source)
        }
    }

    @ReadingConverter
    class JsonbToTasksConverter : Converter<PGobject, TasksWrapper> {
        override fun convert(source: PGobject): TasksWrapper = objectMapper.readValue(source.value!!)
    }
}

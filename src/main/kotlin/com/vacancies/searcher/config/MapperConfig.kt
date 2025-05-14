package com.vacancies.searcher.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
}
package com.vacancies.searcher.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.vacancies.searcher.model.ScrapperConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service


@Service
class ScrapperJobConfigService(
    private val mapper: ObjectMapper
) {
    @Cacheable("configurations")
    fun getConfigurations(): ScrapperConfig = mapper.readValue(
        ClassPathResource("job_scrapper_providers_configurations.json").file,
        ScrapperConfig::class.java
    )
}
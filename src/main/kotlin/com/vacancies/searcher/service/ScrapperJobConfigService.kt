package com.vacancies.searcher.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.vacancies.searcher.model.ScrapperConfig
import com.vacancies.searcher.scrapper.VacancyScrapper
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service


@Service
class ScrapperJobConfigService(
    private val mapper: ObjectMapper,
    private val vacancyScrappers: List<VacancyScrapper>,
) {
    @Cacheable("configurations")
    fun getConfigurations(): ScrapperConfig {
        val config = mapper.readValue(
            ClassPathResource("job_scrapper_providers_configurations.json").inputStream,
            ScrapperConfig::class.java
        )
        val availableScrapers = vacancyScrappers.map { it.getSource().toString() }
        val availableProviders = config.providers.filter { availableScrapers.contains(it.name) }

        return ScrapperConfig(config.version, availableProviders)
    }
}
package com.vacancies.searcher.model

data class ScrapingRequest(
    val inputs: List<ScraperInput>,
)

data class ScraperInput(
    val source: VacancySource,
    val parameters: Map<String, String> = emptyMap()
)
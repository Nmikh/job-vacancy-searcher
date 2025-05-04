package com.vacancies.searcher.model

data class ScrappingRequest(
    val inputs: List<ScrapperInput>,
)

data class ScrapperInput(
    val source: VacancySource,
    val parameters: Map<String, String> = emptyMap()
)
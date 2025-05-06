package com.vacancies.searcher.model

import java.time.Duration


data class ScraperJobResult(
    val status: ScraperJobResultStatus,
    val source: VacancySource,
    val duration: Duration,

    val newVacanciesAmount: Int = 0,
    val deactivatedVacanciesAmount: Int = 0,

    val cause: String? = null,
    val stackTrace: String? = null
)

enum class ScraperJobResultStatus { SUCCESS, FAILURE }
package com.vacancies.searcher.model

import java.time.Duration


sealed class ScrapperJobResult {
    abstract val source: VacancySource
    abstract val duration: Duration

    data class Success(
        val newVacanciesAmount: Int,
        val deactivatedVacanciesAmount: Int,
        override val source: VacancySource,
        override val duration: Duration
    ) : ScrapperJobResult()

    data class Failure(
        val exception: Throwable,
        override val source: VacancySource,
        override val duration: Duration
    ) : ScrapperJobResult()
}
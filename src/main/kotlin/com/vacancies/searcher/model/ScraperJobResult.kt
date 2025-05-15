package com.vacancies.searcher.model

import java.time.Duration

sealed class ScraperJobResult {
    abstract val source: VacancySource
    abstract val duration: Duration

    data class Success(
        override val source: VacancySource,
        override val duration: Duration,
        val successfulUrls: List<String>
    ) : ScraperJobResult()

    data class PartlyFailed(
        override val source: VacancySource,
        override val duration: Duration,
        val successfulUrls: List<String>,
        val failedUrls: List<FailedUrl>
    ) : ScraperJobResult()

    data class Failed(
        override val source: VacancySource,
        override val duration: Duration,
        val cause: String?,
        val stackTrace: String?
    ) : ScraperJobResult()
}

data class FailedUrl(
    val url: String,
    val cause: String? = null,
    val stackTrace: String? = null
)
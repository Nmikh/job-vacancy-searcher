package com.vacancies.searcher.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.vacancies.searcher.model.ScraperJobStatus
import com.vacancies.searcher.model.ScrapingRequest
import com.vacancies.searcher.model.VacancySource
import java.time.LocalDateTime
import java.util.*

data class ScraperJobDto(
    val id: UUID,
    val scrapingRequest: ScrapingRequest,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val scrapingDateTime: LocalDateTime,
    val status: ScraperJobStatus,
    val scrapingResults: List<ScraperJobResultDto>
)

sealed class ScraperJobResultDto {
    abstract val source: VacancySource
    abstract val status: ScraperJobResultStatus

    data class Success(
        override val status: ScraperJobResultStatus = ScraperJobResultStatus.SUCCESS,
        override val source: VacancySource,
        val successfulUrls: List<String>
    ) : ScraperJobResultDto()

    data class PartlyFailed(
        override val status: ScraperJobResultStatus = ScraperJobResultStatus.PARTIALLY_FAILED,
        override val source: VacancySource,
        val successfulUrls: List<String>,
        val failedUrls: List<String>
    ) : ScraperJobResultDto()

    data class Failed(
        override val status: ScraperJobResultStatus = ScraperJobResultStatus.FAILED,
        override val source: VacancySource
    ) : ScraperJobResultDto()
}

enum class ScraperJobResultStatus {
    SUCCESS, PARTIALLY_FAILED, FAILED
}
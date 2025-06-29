package com.vacancies.searcher.model

import com.vacancies.searcher.model.dto.ScraperJobDto
import com.vacancies.searcher.model.dto.ScraperJobProgressDto
import com.vacancies.searcher.model.dto.ScraperJobResultDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.List

@Document("scrapper-job")
data class ScraperJob(
    @Id
    val id: UUID,

    @Field(name = "scraping_request")
    val scrapingRequest: ScrapingRequest,

    @Field("scraping_datetime")
    val scrapingDateTime: LocalDateTime,

    @Field(name = "scraping_status")
    var status: ScraperJobStatus,

    @Field(name = "scraper_job_results")
    var scrapingResults: List<ScraperJobResult> = emptyList()
)

enum class ScraperJobStatus {
    IN_PROGRESS, FINISHED
}

fun ScraperJob.toDto(scraperJobProgress: Map<VacancySource, ScraperJobProgressDto>): ScraperJobDto = ScraperJobDto(
    id = this.id,
    scrapingRequest = this.scrapingRequest,
    scrapingDateTime = this.scrapingDateTime,
    status = this.status,
    scrapingResults = this.scrapingResults.map { it.toDto() },
    scraperJobProgress = scraperJobProgress
)

fun ScraperJobResult.toDto(): ScraperJobResultDto = when (this) {
    is ScraperJobResult.Success -> ScraperJobResultDto.Success(
        source = this.source,
        successfulUrls = this.successfulUrls
    )
    is ScraperJobResult.PartlyFailed -> ScraperJobResultDto.PartlyFailed(
        source = this.source,
        successfulUrls = this.successfulUrls,
        failedUrls = this.failedUrls.map { it.url }
    )
    is ScraperJobResult.Failed -> ScraperJobResultDto.Failed(
        source = this.source
    )
}
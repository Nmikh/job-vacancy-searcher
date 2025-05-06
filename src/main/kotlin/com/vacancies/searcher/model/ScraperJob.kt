package com.vacancies.searcher.model

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
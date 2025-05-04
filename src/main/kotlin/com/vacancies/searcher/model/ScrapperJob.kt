package com.vacancies.searcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("scrapper-job")
data class ScrapperJob(
    @Id
    val id: UUID,

    @Field(name = "scrapping_request")
    val scrappingRequest: ScrappingRequest,

    @Field(name = "scrapper_job_result")
    var scrappingResult: ScrapperJobResult? = null
)
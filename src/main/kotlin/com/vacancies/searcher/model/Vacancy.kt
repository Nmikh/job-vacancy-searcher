package com.vacancies.searcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.*

@Document("vacancies")
data class Vacancy(
    @Id
    val id: UUID,

    @Field(name = "url")
    val url: String,

    @Field(name = "company_name")
    val companyName: String,

    var companyId: UUID? = null,

    @Field(name = "vacancy_title")
    val title: String,

    @Field(name = "vacancy_text")
    val description: String,

    @Field(name = "additional_attributes")
    val additionalAttributes: Map<String, Any?> = emptyMap(),

    @Field(name = "date_posted")
    val datePosted: LocalDateTime,

    @Field(name = "date_scrapped")
    val dateScrapped: LocalDateTime,

    @Field(name = "source")
    val source: VacancySource,

    @Field(name = "active")
    val active: Boolean,

    @Field(name = "tag")
    var tag: VacancyTag
)

data class VacancyPreview(
    val id: UUID,
    val url: String,
    val companyName: String,
    val title: String
)

enum class VacancySource {
    DJINNI,
    DOU,
    TEST_SCRAPPER_SUCCESSFUL,
    TEST_SCRAPPER_PARTLY_FAILED,
    TEST_SCRAPPER_FAILED
}

enum class VacancyTag {
    NEW, NOT_INTERESTED, INTERESTED, APPLIED
}
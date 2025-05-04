package com.vacancies.searcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
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

    @DBRef
    var company: Company? = null,

    @Field(name = "vacancy_title")
    val title: String,

    @Field(name = "vacancy_text")
    val description: String,

    @Field(name = "additional_attributes")
    val additionalAttributes: Map<String, *> = emptyMap<String, Any?>(),

    @Field(name = "date_posted")
    val datePosted: LocalDateTime,

    @Field(name = "date_scrapped")
    val dateScrapped: LocalDateTime,

    @Field(name = "source")
    val source: VacancySource,

    @Field(name = "active")
    val active: Boolean,

    @Field(name = "tags")
    val tags: List<VacancyTags>
)

enum class VacancySource {
    DJINNI,
    DOU,
    TEST_SCRAPPER_1,
    TEST_SCRAPPER_2
}

enum class VacancyTags {
    NEW, NOT_INTERESTED, STARED
}
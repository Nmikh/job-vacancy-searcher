package com.vacancies.searcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("company")
data class Company(
    @Id
    val id: UUID,

    @Field(name = "name")
    val name: String,

    @Field(name = "alternative_names")
    var alternativeNames: MutableSet<String> = mutableSetOf()
)

data class CompanyDTO(
    val name: String,
    var alternativeNames: MutableSet<String> = mutableSetOf()
)
package com.vacancies.searcher.model.dto

data class ScraperJobProgressDto(
    val total: Int,
    val successful: Int,
    val failed: Int
)
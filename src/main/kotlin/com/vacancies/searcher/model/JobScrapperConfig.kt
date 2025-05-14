package com.vacancies.searcher.model

data class ScrapperConfig(
    val version: String,
    val providers: List<Provider>
)

data class Provider(
    val name: String,
    val description: String? = null,
    val parameters: List<ScrapperParameter>
)

data class ScrapperParameter(
    val name: String,
    val description: String? = null,
    val type: ScrapperParameterType,
    val required: Boolean = false,
    val multiple: Boolean = false,
    val options: List<ScrapperParameterOption>? = null,
)

enum class ScrapperParameterType {
    STRING,
    ENUM,
    NUMBER,
    BOOLEAN,
    DATE
}

data class ScrapperParameterOption(
    val value: String,
    val label: String
)
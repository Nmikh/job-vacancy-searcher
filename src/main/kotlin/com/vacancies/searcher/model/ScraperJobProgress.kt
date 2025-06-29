package com.vacancies.searcher.model

import java.util.concurrent.atomic.AtomicInteger

data class ScraperJobProgress(
    val total: AtomicInteger = AtomicInteger(0),
    val successful: AtomicInteger = AtomicInteger(0),
    val failed: AtomicInteger = AtomicInteger(0)
)
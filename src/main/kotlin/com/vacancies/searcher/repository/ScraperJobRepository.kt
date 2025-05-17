package com.vacancies.searcher.repository

import com.vacancies.searcher.model.ScraperJob
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ScraperJobRepository : MongoRepository<ScraperJob, UUID> {
    fun findFirstByOrderByScrapingDateTime(): Optional<ScraperJob>
}
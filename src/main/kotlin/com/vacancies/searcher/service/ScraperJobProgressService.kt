package com.vacancies.searcher.service

import com.vacancies.searcher.model.ScraperJobProgress
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.model.dto.ScraperJobProgressDto
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class ScraperJobProgressService {
    private val scraperJobsProgress =
        ConcurrentHashMap<UUID, ConcurrentHashMap<VacancySource, ScraperJobProgress>>()

    fun addTotal(jobId: UUID, source: VacancySource, total: Int) {
        val jobMap = scraperJobsProgress.computeIfAbsent(jobId) { ConcurrentHashMap() }
        val progress = jobMap.computeIfAbsent(source) { ScraperJobProgress() }
        progress.total.set(total)
    }

    fun increaseSuccess(jobId: UUID, source: VacancySource) {
        scraperJobsProgress[jobId]?.get(source)?.successful?.incrementAndGet()
    }

    fun increaseFailed(jobId: UUID, source: VacancySource) {
        scraperJobsProgress[jobId]?.get(source)?.failed?.incrementAndGet()
    }

    fun getProgress(jobId: UUID): Map<VacancySource, ScraperJobProgressDto> {
        return scraperJobsProgress[jobId]?.mapValues { (_, value) ->
            ScraperJobProgressDto(
                total = value.total.get(),
                successful = value.successful.get(),
                failed = value.failed.get()
            )
        } ?: emptyMap()
    }
}


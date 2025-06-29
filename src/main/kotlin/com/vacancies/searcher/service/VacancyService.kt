package com.vacancies.searcher.service

import com.vacancies.searcher.model.ScraperJob
import com.vacancies.searcher.model.ScraperJobStatus
import com.vacancies.searcher.model.ScrapingRequest
import com.vacancies.searcher.model.VacancyPreview
import com.vacancies.searcher.model.VacancyTag
import com.vacancies.searcher.repository.ScraperJobRepository
import com.vacancies.searcher.repository.VacancyRepository
import com.vacancies.searcher.scrapper.VacancyScrapper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


@Service
class VacancyService(
    private val vacancyScrappers: List<VacancyScrapper>,
    private val scraperJobRepository: ScraperJobRepository,
    private val vacancyRepository: VacancyRepository
) {

    @Async
    fun startScrapping(request: ScrapingRequest, jobId: UUID) {
        val scraperJob = ScraperJob(jobId, request, LocalDateTime.now(ZoneOffset.UTC), ScraperJobStatus.IN_PROGRESS)
        scraperJobRepository.save(scraperJob)

        val results = vacancyScrappers
            .mapNotNull { scrapper ->
                request.inputs
                    .find { it.source == scrapper.getSource() }
                    ?.let { scrapper.scrapeVacancies(it.parameters, jobId) }
            }
        scraperJob.status = ScraperJobStatus.FINISHED
        scraperJob.scrapingResults = results

        scraperJobRepository.save(scraperJob)
    }

    fun getScrappingStatus(jobId: UUID) = scraperJobRepository.findById(jobId)

    fun getLatestScrappingStatus() = scraperJobRepository.findFirstByOrderByScrapingDateTimeDesc()

    fun getVacanciesPreviews(tag: VacancyTag): List<VacancyPreview> =
        vacancyRepository.findAllByTagAndActive(tag, true)
            .map { VacancyPreview(it.id, it.url, it.companyName, it.title, it.source) }

    fun getVacancy(id: UUID) = vacancyRepository.findById(id)

    fun changeTag(id: UUID, vacancyTag: VacancyTag) {
        vacancyRepository.findById(id).ifPresent(
            {
                it.tag = vacancyTag
                vacancyRepository.save(it)
            }
        )
    }
}
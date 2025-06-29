package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.FailedUrl
import com.vacancies.searcher.model.ScraperJobResult
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.VacancyRepository
import com.vacancies.searcher.service.ScraperJobProgressService
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*


interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, List<String>>, jobId: UUID): ScraperJobResult

    fun getSource(): VacancySource
}

abstract class AbstractVacancyScrapper(
    private val vacancyRepository: VacancyRepository,
    private val scraperJobProgressService: ScraperJobProgressService
) : VacancyScrapper {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val SLEEP_TIME = 30000L
    }

    override fun scrapeVacancies(parameters: Map<String, List<String>>, jobId: UUID): ScraperJobResult {
        val startTime = Instant.now()
        val source = getSource()

        log.info("Starting scraping for source={} with jobId={}", source, jobId)

        val existingUrls = vacancyRepository.findAllBySourceAndActive(source, true).map { it.url }
        val vacancyLinksResult = runCatching { getVacancyLinks(parameters) }
        val siteUrls = vacancyLinksResult.getOrElse { exception ->
            log.error(
                "Failed to get vacancy links for source={} with jobId={}: {}",
                source,
                jobId,
                exception.message,
                exception
            )
            return ScraperJobResult.Failed(
                source = source,
                duration = Duration.between(startTime, Instant.now()),
                cause = exception.message,
                stackTrace = exception.stackTraceToString()
            )
        }

        val newUrls = siteUrls.filterNot { it in existingUrls }
        scraperJobProgressService.addTotal(jobId, source, newUrls.size)
        log.info(
            "Found {} total URLs, {} new URLs to scrape for source={} with jobId={}",
            siteUrls.size,
            newUrls.size,
            source,
            jobId
        )

        val (successfulUrlsPairs, failedUrlsPairs) =
            newUrls.map { url ->
                url to getVacancyResult(url, parameters)
                    .also { result ->
                        if (result.isSuccess) {
                            scraperJobProgressService.increaseSuccess(jobId, source)
                            log.info("Successfully scraped URL={} for source={} with jobId={}", url, source, jobId)
                        } else {
                            scraperJobProgressService.increaseFailed(jobId, source)
                            val ex = result.exceptionOrNull()
                            log.warn(
                                "Failed to scrape URL={} for source={} with jobId={}: {}",
                                url,
                                source,
                                jobId,
                                ex?.message
                            )
                        }
                    }
            }.partition { it.second.isSuccess }

        val successfulUrls = successfulUrlsPairs.map { it.first }
        val failedUrls = failedUrlsPairs.map { (url, result) ->
            val ex = result.exceptionOrNull()
            FailedUrl(url, ex?.message, ex?.stackTraceToString())
        }

        val outdatedUrls = existingUrls.filterNot { it in siteUrls }
        vacancyRepository.updateActiveStatus(outdatedUrls, false)

        val duration = Duration.between(startTime, Instant.now())

        log.info(
            "Scraping finished for source={} with jobId={} in {}s: success={}, failed={}, outdated={}",
            source, jobId, duration.seconds, successfulUrls.size, failedUrls.size, outdatedUrls.size
        )

        if (failedUrls.isNotEmpty()) {
            return ScraperJobResult.PartlyFailed(
                source = source,
                duration = duration,
                successfulUrls = successfulUrls,
                failedUrls = failedUrls
            )
        }

        return ScraperJobResult.Success(
            source = source,
            duration = duration,
            successfulUrls = successfulUrls
        )
    }

    private fun getVacancyResult(url: String, parameters: Map<String, List<String>>): Result<Vacancy> =
        runCatching {
            val vacancy = getVacancy(url, parameters)
            vacancyRepository.save(vacancy)
            vacancy
        }

    protected abstract fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy

    protected abstract fun getVacancyLinks(parameters: Map<String, List<String>>): List<String>
}
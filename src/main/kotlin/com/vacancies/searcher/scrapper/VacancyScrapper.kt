package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.FailedUrl
import com.vacancies.searcher.model.ScraperJobResult
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.CompanyRepository
import com.vacancies.searcher.repository.VacancyRepository
import java.time.Duration
import java.time.Instant


interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, String>): ScraperJobResult

    fun getSource(): VacancySource
}

abstract class AbstractVacancyScrapper(
    private val vacancyRepository: VacancyRepository,
    private val companyRepository: CompanyRepository
) : VacancyScrapper {

    companion object {
        const val SLEEP_TIME = 30000L
    }

    override fun scrapeVacancies(parameters: Map<String, String>): ScraperJobResult {
        val startTime = Instant.now()
        val source = getSource()

        return runCatching {
            val existingUrls = vacancyRepository.findAllBySourceAndActive(getSource(), true).map { it.url }
            val siteUrls = getVacancyLinks(parameters)

            val successfulUrls = mutableListOf<String>()
            val failedUrls = mutableListOf<FailedUrl>()
            val newVacancies = siteUrls
                .filterNot { it in existingUrls }
                .mapNotNull { url ->
                    getVacancyResult(url, parameters).fold(
                        onSuccess = { successfulUrls.add(url); it },
                        onFailure = {
                            failedUrls.add(FailedUrl(url, it.message, it.stackTraceToString()))
                            null
                        })
                }
                .map { setUpCompany(it) }

            vacancyRepository.saveAll(newVacancies)

            val outdatedVacanciesUrls = existingUrls.filterNot { it in siteUrls }
            vacancyRepository.updateActiveStatus(outdatedVacanciesUrls, false)

            if (failedUrls.isNotEmpty()) {
                return ScraperJobResult.PartlyFailed(
                    source = source,
                    duration = Duration.between(startTime, Instant.now()),
                    successfulUrls = successfulUrls,
                    failedUrls = failedUrls
                )
            }

            return ScraperJobResult.Success(
                source = source,
                duration = Duration.between(startTime, Instant.now()),
                successfulUrls = successfulUrls
            )
        }.getOrElse { ex ->
            ScraperJobResult.Failed(
                source = source,
                duration = Duration.between(startTime, Instant.now()),
                cause = ex.cause?.toString(),
                stackTrace = ex.stackTraceToString()
            )
        }
    }

    private fun setUpCompany(vacancy: Vacancy): Vacancy {
        companyRepository
            .findOneByAlternativeNamesIn(vacancy.companyName)
            ?.let { vacancy.companyId = it.id }

        return vacancy
    }

    private fun getVacancyResult(url: String, parameters: Map<String, String>): Result<Vacancy> =
        runCatching { return Result.success(getVacancy(url, parameters)) }.getOrElse { Result.failure(it) }

    protected abstract fun getVacancy(url: String, parameters: Map<String, String>): Vacancy

    protected abstract fun getVacancyLinks(parameters: Map<String, String>): List<String>

}
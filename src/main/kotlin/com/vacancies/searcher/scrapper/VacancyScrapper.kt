package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.ScrapperJobResult
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.VacancyRepository
import java.time.Duration
import java.time.Instant


interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, String>): ScrapperJobResult
}

abstract class AbstractVacancyScrapper(
    private val vacancyRepository: VacancyRepository
) : VacancyScrapper {
    override fun scrapeVacancies(parameters: Map<String, String>): ScrapperJobResult {
        val startTime = Instant.now()
        val source = getSource()

        return runCatching {
            val existingUrls = vacancyRepository.findAllBySourceAndActive(getSource(), true).map { it.url }
            val siteUrls = getVacancyLinks(parameters)

            val newVacancies = siteUrls
                .filterNot { it in existingUrls }
                .map { link -> getVacancy(link, parameters) }

            vacancyRepository.saveAll(newVacancies)

            val outdatedVacanciesUrls = existingUrls.filterNot { it in siteUrls }
            vacancyRepository.updateActiveStatus(outdatedVacanciesUrls, false)

            ScrapperJobResult.Success(
                source = source,
                newVacanciesAmount = newVacancies.size,
                deactivatedVacanciesAmount = outdatedVacanciesUrls.size,
                duration = Duration.between(startTime, Instant.now())
            )
        }.getOrElse { ex ->
            ScrapperJobResult.Failure(
                source = source,
                exception = ex,
                duration = Duration.between(startTime, Instant.now())
            )
        }
    }

    protected abstract fun getVacancyLinks(parameters: Map<String, String>): List<String>

    protected abstract fun getVacancy(url: String, parameters: Map<String, String>): Vacancy

    protected abstract fun getSource(): VacancySource
}
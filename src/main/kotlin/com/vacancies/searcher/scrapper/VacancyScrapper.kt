package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.ScrapperJobResult
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.CompanyRepository
import com.vacancies.searcher.repository.VacancyRepository
import java.time.Duration
import java.time.Instant


interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, String>): ScrapperJobResult

    fun getSource(): VacancySource
}

abstract class AbstractVacancyScrapper(
    private val vacancyRepository: VacancyRepository,
    private val companyRepository: CompanyRepository
) : VacancyScrapper {
    override fun scrapeVacancies(parameters: Map<String, String>): ScrapperJobResult {
        val startTime = Instant.now()
        val source = getSource()

        return runCatching {
            val existingUrls = vacancyRepository.findAllBySourceAndActive(getSource(), true).map { it.url }
            val siteUrls = getVacancyLinks(parameters)

            val newVacancies = siteUrls
                .filterNot { it in existingUrls }
                .map { getVacancy(it, parameters) }
                .map { setUpCompany(it) }

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

    private fun setUpCompany(vacancy: Vacancy): Vacancy {
        companyRepository
            .findOneByAlternativeNamesContaining(vacancy.companyName)
            ?.let { vacancy.company = it }

        return vacancy
    }

    protected abstract fun getVacancyLinks(parameters: Map<String, String>): List<String>

    protected abstract fun getVacancy(url: String, parameters: Map<String, String>): Vacancy
}
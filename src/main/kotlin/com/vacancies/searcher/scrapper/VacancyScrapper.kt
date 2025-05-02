package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.JobVacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.JobVacancyRepository


interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, String>)
}

abstract class AbstractVacancyScrapper(
    private val jobVacancyRepository: JobVacancyRepository
) : VacancyScrapper {
    override fun scrapeVacancies(parameters: Map<String, String>) {
        val existingUrls = jobVacancyRepository.findAllBySourceAndActive(getSource(), true).map { it.url }
        val siteUrls = getVacancyLinks(parameters)

        val newVacancies = siteUrls
            .filterNot { it in existingUrls }
            .map { link -> getJobVacancy(link, parameters) }

        jobVacancyRepository.saveAll(newVacancies)

        val outdatedVacancies = existingUrls.filterNot { it in siteUrls }
        jobVacancyRepository.updateActiveStatus(outdatedVacancies, false)

    }

    protected abstract fun getVacancyLinks(parameters: Map<String, String>): List<String>

    protected abstract fun getJobVacancy(url: String, parameters: Map<String, String>): JobVacancy

    protected abstract fun getSource(): VacancySource
}
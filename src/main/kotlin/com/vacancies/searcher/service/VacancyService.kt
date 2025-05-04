package com.vacancies.searcher.service

import com.vacancies.searcher.model.ScrapperInput
import com.vacancies.searcher.model.ScrapperJobResult
import com.vacancies.searcher.model.ScrappingRequest
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.VacancyRepository
import com.vacancies.searcher.scrapper.VacancyScrapper
import org.springframework.stereotype.Service

@Service
class VacancyService(
    private val vacancyScrappers: List<VacancyScrapper>,
    private val vacancyRepository: VacancyRepository
) {

    fun startScrapping(request: ScrappingRequest): List<ScrapperJobResult> {
        return vacancyScrappers
            .mapNotNull { scrapper ->
                request.inputs
                    .find { it.source == scrapper.getSource() }
                    ?.let { scrapper.scrapeVacancies(it.parameters) }
            }
    }

}
package com.vacancies.searcher.controller

import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.ScraperJobRepository
import com.vacancies.searcher.repository.VacancyRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scrapper/admin")
class AdminController(
    private val jobRepository: ScraperJobRepository,
    private val vacancyRepository: VacancyRepository
) {
    @DeleteMapping("/jobs/all")
    fun removeAllJobs(): ResponseEntity<Unit> {
        jobRepository.deleteAll()
        vacancyRepository.deleteAll()
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/vacancies/test")
    fun removeTestVacancies(): ResponseEntity<Unit> {
        vacancyRepository.deleteAllBySourceIn(
            listOf(
                VacancySource.TEST_SCRAPPER_FAILED,
                VacancySource.TEST_SCRAPPER_SUCCESSFUL,
                VacancySource.TEST_SCRAPPER_PARTLY_FAILED
            )
        )
        return ResponseEntity.ok().build()
    }
}
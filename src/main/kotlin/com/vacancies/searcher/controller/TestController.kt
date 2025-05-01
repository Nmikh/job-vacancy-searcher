package com.vacancies.searcher.controller

import com.vacancies.searcher.scrapper.JobVacancy
import com.vacancies.searcher.scrapper.JobVacancyRepository
import com.vacancies.searcher.scrapper.VacancyScrapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val scrappers: List<VacancyScrapper>,
    private val jobVacancyRepository: JobVacancyRepository
) {

    @PostMapping("/start")
    fun startScrape(@RequestBody parameters: Map<String, String>): ResponseEntity<String> {
        scrappers.forEach { scrapper -> scrapper.scrapeVacancies(parameters) }
        return ResponseEntity.ok("started");
    }

    @GetMapping("/jobs")
    fun getAll(): List<JobVacancy> = jobVacancyRepository.findAll()

    @DeleteMapping("/jobs")
    fun deleteAll() = jobVacancyRepository.deleteAll()
}
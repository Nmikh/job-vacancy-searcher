package com.vacancies.searcher.controller

import com.vacancies.searcher.model.ScrapperJobResult
import com.vacancies.searcher.model.ScrappingRequest
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.repository.CompanyRepository
import com.vacancies.searcher.repository.VacancyRepository
import com.vacancies.searcher.scrapper.VacancyScrapper
import com.vacancies.searcher.service.VacancyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/scrapper/job")
class ScrapperJobController(
    private val scrappers: List<VacancyScrapper>,
    private val vacancyRepository: VacancyRepository,
    private val vacancyService: VacancyService
) {

    @PostMapping("/start")
    fun startScrape(@RequestBody request: ScrappingRequest): ResponseEntity<List<ScrapperJobResult>> {
        val jobResults = vacancyService.startScrapping(request)
        return ResponseEntity.ok(jobResults);
    }

    @GetMapping("/jobs")
    fun getAll(): List<Vacancy> = vacancyRepository.findAll()

    @DeleteMapping("/jobs")
    fun deleteAll() = vacancyRepository.deleteAll()

//    @GetMapping("/create")
//    fun addTests() {
//        val jobVacancy =
//            JobVacancy("url1", "company1", "title1", "description1", mapOf("1" to "1"), VacancySource.DJINNI, true)
//        val jobVacancy2 =
//            JobVacancy("url2", "company2", "title2", "description2", mapOf("2" to "2"), VacancySource.DJINNI, true)
//
//        jobVacancyRepository.save(jobVacancy);
//        jobVacancyRepository.save(jobVacancy2);
//    }
}
package com.vacancies.searcher.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.ScraperJobRepository
import com.vacancies.searcher.repository.VacancyRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scrapper/admin")
class AdminController(
    private val jobRepository: ScraperJobRepository,
    private val vacancyRepository: VacancyRepository,
    private val mapper: ObjectMapper
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

    @GetMapping("/vacancies/export", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun exportVacancies(): ResponseEntity<Resource> {
        val vacancies = vacancyRepository.findAll()
        val jsonBytes = mapper.writeValueAsBytes(vacancies)
        val resource = ByteArrayResource(jsonBytes)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vacancies.json")
            .contentType(MediaType.APPLICATION_JSON)
            .contentLength(jsonBytes.size.toLong())
            .body(resource)
    }
}
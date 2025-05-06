package com.vacancies.searcher.controller

import com.vacancies.searcher.model.ScraperJob
import com.vacancies.searcher.model.ScrapingRequest
import com.vacancies.searcher.service.VacancyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/scrapper/job")
class ScrapperJobController(
    private val vacancyService: VacancyService
) {
    @PostMapping("start")
    fun startScrape(@RequestBody request: ScrapingRequest): ResponseEntity<UUID> {
        val jobId = UUID.randomUUID()
        vacancyService.startScrapping(request, jobId)

        return ResponseEntity.ok(jobId);
    }

    @GetMapping("{jobId}")
    fun jobStatus(@PathVariable jobId: UUID): ResponseEntity<ScraperJob> =
        vacancyService.getScrappingStatus(jobId)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
}
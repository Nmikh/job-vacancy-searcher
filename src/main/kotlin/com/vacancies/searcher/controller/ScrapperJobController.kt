package com.vacancies.searcher.controller

import com.vacancies.searcher.model.ScrapingRequest
import com.vacancies.searcher.model.ScrapperConfig
import com.vacancies.searcher.model.dto.ScraperJobDto
import com.vacancies.searcher.model.dto.StartJobResponse
import com.vacancies.searcher.model.toDto
import com.vacancies.searcher.service.ScraperJobProgressService
import com.vacancies.searcher.service.ScrapperJobConfigService
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
    private val vacancyService: VacancyService,
    private val scrapperJobConfigService: ScrapperJobConfigService,
    private val scraperJobProgressService: ScraperJobProgressService
) {
    @PostMapping("start")
    fun startScrape(@RequestBody request: ScrapingRequest): ResponseEntity<StartJobResponse> {
        val jobId = UUID.randomUUID()
        vacancyService.startScrapping(request, jobId)

        return ResponseEntity.ok(StartJobResponse(jobId))
    }

    @GetMapping("{jobId}")
    fun getJobStatus(@PathVariable jobId: UUID): ResponseEntity<ScraperJobDto> =
        vacancyService.getScrappingStatus(jobId)
            .map { it.toDto(scraperJobProgressService.getProgress(it.id)) }
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @GetMapping("latest")
    fun getLatestJobStatus(): ResponseEntity<ScraperJobDto> =
        vacancyService.getLatestScrappingStatus()
            .map { it.toDto(scraperJobProgressService.getProgress(it.id)) }
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @GetMapping("configurations")
    fun getConfigurations(): ResponseEntity<ScrapperConfig> =
        ResponseEntity.ok(scrapperJobConfigService.getConfigurations())

}
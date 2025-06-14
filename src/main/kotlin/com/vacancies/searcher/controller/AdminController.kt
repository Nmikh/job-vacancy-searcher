package com.vacancies.searcher.controller

import com.vacancies.searcher.repository.ScraperJobRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scrapper/admin")
class AdminController(
    private val jobRepository: ScraperJobRepository
) {
    @DeleteMapping("/jobs/all")
    fun removeAllJobs(): ResponseEntity<Unit> {
        jobRepository.deleteAll()
        return ResponseEntity.ok().build()
    }
}
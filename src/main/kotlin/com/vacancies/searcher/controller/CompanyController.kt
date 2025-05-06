package com.vacancies.searcher.controller

import com.vacancies.searcher.model.Company
import com.vacancies.searcher.model.CompanyDTO
import com.vacancies.searcher.service.CompanyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/company")
class CompanyController(
    private val companyService: CompanyService
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<Company>> =
        ResponseEntity.ok(companyService.findAll())

    @GetMapping("{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<Company> {
        val company = companyService.findById(id)

        return if (company != null) ResponseEntity.ok(company)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun create(@RequestBody company: CompanyDTO): ResponseEntity<Company> {
        val created = companyService.create(company)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PostMapping("{id}/alternative-names")
    fun addAlternativeNames(
        @PathVariable id: UUID,
        @RequestBody names: List<String>
    ): ResponseEntity<Unit> {
        companyService.addAlternativeNames(id, names)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{id}/alternative-names")
    fun removeAlternativeNames(
        @PathVariable id: UUID,
        @RequestBody names: List<String>
    ): ResponseEntity<Unit> {
        companyService.removeAlternativeNames(id, names)
        return ResponseEntity.noContent().build()
    }
}
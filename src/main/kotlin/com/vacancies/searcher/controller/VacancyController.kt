package com.vacancies.searcher.controller

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancyPreview
import com.vacancies.searcher.model.VacancyTag
import com.vacancies.searcher.service.VacancyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/vacancy")
class VacancyController(
    private val vacancyService: VacancyService
) {

    @GetMapping("preview/{tag}")
    fun getVacanciesPreview(@PathVariable tag: VacancyTag): ResponseEntity<List<VacancyPreview>> {
        val vacanciesPreviews = vacancyService.getVacanciesPreviews(tag)
        return ResponseEntity.ok(vacanciesPreviews)
    }

    @GetMapping("{id}")
    fun getVacancy(@PathVariable id: UUID): ResponseEntity<Vacancy> =
        vacancyService.getVacancy(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PatchMapping("{id}/tag")
    fun changeVacancyTag(@PathVariable id: UUID, @RequestParam tag: VacancyTag): ResponseEntity<Unit> {
        vacancyService.changeTag(id, tag)
        return ResponseEntity.ok().build()
    }
}
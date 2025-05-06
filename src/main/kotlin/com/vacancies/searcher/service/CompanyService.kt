package com.vacancies.searcher.service

import com.vacancies.searcher.model.Company
import com.vacancies.searcher.model.CompanyDTO
import com.vacancies.searcher.repository.CompanyRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
) {

    fun findAll(): List<Company> = companyRepository.findAll()

    fun findById(id: UUID): Company? = companyRepository.findById(id).getOrNull()

    fun create(companyDTO: CompanyDTO): Company {
        val company = Company(UUID.randomUUID(), companyDTO.name, companyDTO.alternativeNames)
        return companyRepository.save(company)
    }

    fun addAlternativeNames(companyId: UUID, newAlternativeNames: List<String>) {
        val company = companyRepository.findById(companyId).getOrNull() ?: return
        val uniqueNames = newAlternativeNames - company.alternativeNames.toSet()
        if (uniqueNames.isNotEmpty()) {
            company.alternativeNames.addAll(uniqueNames)
            companyRepository.save(company)
        }
    }

    fun removeAlternativeNames(companyId: UUID, removedAlternativeNames: List<String>) {
        val company = companyRepository.findById(companyId).getOrNull() ?: return

        company.alternativeNames.removeAll(removedAlternativeNames)
        companyRepository.save(company)
    }
}
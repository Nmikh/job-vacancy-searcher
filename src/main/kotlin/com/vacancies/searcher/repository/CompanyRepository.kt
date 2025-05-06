package com.vacancies.searcher.repository

import com.vacancies.searcher.model.Company
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyRepository : MongoRepository<Company, UUID> {
    fun findOneByAlternativeNamesIn(companyName: String): Company?
}
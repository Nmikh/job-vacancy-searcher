package com.vacancies.searcher.repository

import com.vacancies.searcher.model.Company
import com.vacancies.searcher.model.ScrapperJob
import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VacancyRepository : MongoRepository<Vacancy, UUID> {
    fun findAllBySourceAndActive(source: VacancySource, active: Boolean): Collection<Vacancy>

    @Query("{ 'url' :  { \$in: ?0 } }")
    @Update("{ '\$set' : { 'active' : ?1 } }")
    fun updateActiveStatus(urls: List<String>, active: Boolean)
}

@Repository
interface CompanyRepository : MongoRepository<Company, UUID> {
    fun findOneByAlternativeNamesContaining(companyName: String): Company?
}

@Repository
interface ScrapperJobRepository : MongoRepository<ScrapperJob, UUID>
package com.vacancies.searcher.repository

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.model.VacancyTag
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VacancyRepository : MongoRepository<Vacancy, UUID> {
    fun findAllBySourceAndActive(source: VacancySource, active: Boolean): List<Vacancy>

    fun findAllByTagAndActive(tag: VacancyTag, active: Boolean): List<Vacancy>

    @Query("{ 'url' :  { \$in: ?0 } }")
    @Update("{ '\$set' : { 'active' : ?1 } }")
    fun updateActiveStatus(urls: List<String>, active: Boolean)
}
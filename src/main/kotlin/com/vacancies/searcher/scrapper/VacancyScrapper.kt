package com.vacancies.searcher.scrapper

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

enum class VacancySource {
    DJINNI, DOU
}

@Document("job-vacancy")
data class JobVacancy(
    @Id
    val url: String,

    @Field(name = "company_name")
    val company: String,

    @Field(name = "vacancy_title")
    val title: String,

    @Field(name = "vacancy_text")
    val description: String,

    @Field(name = "additional_attributes")
    val additionalAttributes: Map<String, *>,

    @Field(name = "source")
    val source: VacancySource,

    @Field(name = "active")
    val active: Boolean


)

@Repository
interface JobVacancyRepository : MongoRepository<JobVacancy, String> {
    fun findAllBySourceAndActive(source: VacancySource, active: Boolean): Collection<JobVacancy>
}

interface VacancyScrapper {
    fun scrapeVacancies(parameters: Map<String, String>)
}

abstract class AbstractVacancyScrapper(
    private val jobVacancyRepository: JobVacancyRepository
) : VacancyScrapper {
    override fun scrapeVacancies(parameters: Map<String, String>) {
        val existingUrls = jobVacancyRepository.findAllBySourceAndActive(getSource(), true).map { it.url }
        val newVacancies = getVacancyLinks(parameters)
            .filterNot { existingUrls.contains(it) }
            .map { link -> getJobVacancy(link, parameters) }

        jobVacancyRepository.saveAll(newVacancies)
    }

    protected abstract fun getVacancyLinks(parameters: Map<String, String>): List<String>

    protected abstract fun getJobVacancy(url: String, parameters: Map<String, String>): JobVacancy

    protected abstract fun getSource(): VacancySource
}

@Service
class DjinniScrapper(jobVacancyRepository: JobVacancyRepository) : AbstractVacancyScrapper(jobVacancyRepository) {
    companion object {
        private const val BASE_URL = "https://djinni.co"
        private const val JOB_SEARCH_URL =
            "$BASE_URL/jobs/?primary_keyword=Java&exp_level=4y&exp_level=5y&exp_level=6y&exp_level=7y&employment=remote&region=UKR"
    }

    override fun getVacancyLinks(parameters: Map<String, String>): List<String> {
        val sessionCookie = parameters["session_cookie"] ?: error("session_cookie is required")
        val client = OkHttpClient.Builder().followRedirects(false).build()

        return generateSequence(1) { it + 1 }
            .map { page ->
                val request = Request.Builder()
                    .url("$JOB_SEARCH_URL&page=$page")
                    .header("Cookie", sessionCookie)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.code != 200 || body == null) return@map null

                return@map Jsoup.parse(body).select("a.job-item__title-link")
                    .map { BASE_URL + it.attr("href") }
            }
            .takeWhile { it != null }
            .flatMap { it ?: emptyList() }
            .toList()
    }


    override fun getJobVacancy(url: String, parameters: Map<String, String>): JobVacancy {
        val document = Jsoup.connect(url).timeout(10 * 1000).get()

        val vacancy = document.selectFirst("script[type=application/ld+json]")?.html()

        val json = JSONObject(vacancy)
        val description = json["description"].toString()
        val title = json["title"].toString()
        val company = (json["hiringOrganization"] as JSONObject)["name"].toString()

        val attributes = document.select("li.mb-1").mapNotNull { it.selectFirst("div.col")?.text() }
        val additionalAttributes = mapOf("attributes" to attributes)

        return JobVacancy(url, company, title, description, additionalAttributes, getSource(), true)
    }

    override fun getSource(): VacancySource = VacancySource.DJINNI
}


//class DOUScrapper : AbstractVacancyScrapper() {
//    override fun getVacancyLinks(url: String): List<String> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getJobVacancy(url: String): JobVacancy {
//        TODO("Not yet implemented")
//    }
//
//}

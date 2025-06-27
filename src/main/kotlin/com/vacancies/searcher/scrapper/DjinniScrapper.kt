package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.model.VacancyTag
import com.vacancies.searcher.repository.CompanyRepository
import com.vacancies.searcher.repository.VacancyRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.djinni"],
    havingValue = "true",
    matchIfMissing = false
)
class DjinniScrapper(
    vacancyRepository: VacancyRepository,
    companyRepository: CompanyRepository
) : AbstractVacancyScrapper(vacancyRepository, companyRepository) {
    companion object {
        private const val BASE_URL = "https://djinni.co"
        private const val JOB_SEARCH_URL =
            "$BASE_URL/jobs/?primary_keyword=Java&exp_level=4y&exp_level=5y&exp_level=6y&exp_level=7y&employment=remote&region=UKR"
    }

    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        val ssoLogin = parameters["sso_login"] ?: error("sso_login is required")
        val client = OkHttpClient.Builder().followRedirects(false).build()

        return generateSequence(1) { it + 1 }
            .mapNotNull { page ->
                val request = Request.Builder()
                    .url("$JOB_SEARCH_URL&page=$page")
                    .header("Cookie", ssoLogin[0])
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.code != 200 || body == null) return@mapNotNull emptyList()

                sleep(SLEEP_TIME)

                return@mapNotNull Jsoup.parse(body).select("a.job-item__title-link")
                    .map { BASE_URL + it.attr("href") }
            }
            .takeWhile { it.isNotEmpty() }
            .flatMap { it }
            .toList()
    }


    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        sleep(SLEEP_TIME)

        val document = Jsoup.connect(url).timeout(10 * 1000).header("Cookie", parameters["sso_login"]!![0]).get()
        val vacancy = document.selectFirst("script[type=application/ld+json]")?.html()
        val json = JSONObject(vacancy)
        val attributes = document.select("li.mb-1").mapNotNull { it.selectFirst("div.col")?.text() }
        val additionalAttributes = mapOf("attributes" to attributes)

        return Vacancy(
            id = UUID.randomUUID(),
            url = url,
            companyName = (json["hiringOrganization"] as JSONObject)["name"].toString(),
            title = json["title"].toString(),
            description = json["description"].toString(),
            additionalAttributes = additionalAttributes,
            source = getSource(),
            dateScrapped = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC),
            datePosted = LocalDateTime.parse(json["datePosted"].toString(), DateTimeFormatter.ISO_DATE_TIME),
            active = true,
            tag = VacancyTag.NEW
        )
    }

    override fun getSource(): VacancySource = VacancySource.DJINNI
}
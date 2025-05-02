package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.JobVacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.JobVacancyRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.time.LocalDateTime

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.testScrapper1"],
    havingValue = "true",
    matchIfMissing = false
)
class TestScrapper1(jobVacancyRepository: JobVacancyRepository) : AbstractVacancyScrapper(jobVacancyRepository) {
    override fun getVacancyLinks(parameters: Map<String, String>): List<String> {
        sleep(30000)

        return listOf(
            "https://example.com/testScrapper1/job1",
            "https://example.com/testScrapper1/job2"
        )
    }

    override fun getJobVacancy(url: String, parameters: Map<String, String>): JobVacancy {
        sleep(30000)

        return JobVacancy(
            url = url,
            company = "Test Scrapper1",
            title = "Senior Java Developer",
            description = "This is a test job description for $url.",
            additionalAttributes = mapOf("location" to "Remote", "level" to "Senior"),
            datePosted = LocalDateTime.now().minusDays(5),
            dateScrapped = LocalDateTime.now(),
            source = getSource(),
            active = true
        )
    }

    override fun getSource(): VacancySource = VacancySource.TEST_SCRAPPER_1

}

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.testScrapper2"],
    havingValue = "true",
    matchIfMissing = false
)
class TestScrapper2(jobVacancyRepository: JobVacancyRepository) : AbstractVacancyScrapper(jobVacancyRepository) {
    override fun getVacancyLinks(parameters: Map<String, String>): List<String> {
        sleep(30000)

        return listOf(
            "https://example.com/testScrapper2/job1",
            "https://example.com/testScrapper2/job2"
        )
    }

    override fun getJobVacancy(url: String, parameters: Map<String, String>): JobVacancy {
        sleep(30000)

        return JobVacancy(
            url = url,
            company = "Test Scrapper2",
            title = "Senior Java Developer",
            description = "This is a test job description for $url.",
            additionalAttributes = mapOf("location" to "Remote", "level" to "Senior"),
            datePosted = LocalDateTime.now().minusDays(5),
            dateScrapped = LocalDateTime.now(),
            source = getSource(),
            active = true
        )
    }

    override fun getSource(): VacancySource = VacancySource.TEST_SCRAPPER_2
}

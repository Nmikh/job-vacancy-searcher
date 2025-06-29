package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.model.VacancyTag
import com.vacancies.searcher.repository.VacancyRepository
import com.vacancies.searcher.service.ScraperJobProgressService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.util.*

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.testScrapperSuccessful"],
    havingValue = "true",
    matchIfMissing = false
)
class TestScrapperSuccessful(
    vacancyRepository: VacancyRepository,
    scraperJobProgressService: ScraperJobProgressService
) : AbstractVacancyScrapper(vacancyRepository, scraperJobProgressService) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(30000)

        return listOf(
            "https://example.com/TestScrapperSuccessful/job1",
            "https://example.com/TestScrapperSuccessful/job2",
            "https://example.com/TestScrapperSuccessful/job3",
            "https://example.com/TestScrapperSuccessful/job4",
            "https://example.com/TestScrapperSuccessful/job5",
            "https://example.com/TestScrapperSuccessful/job6",
            "https://example.com/TestScrapperSuccessful/job7",
            "https://example.com/TestScrapperSuccessful/job8",
            "https://example.com/TestScrapperSuccessful/job9",
        )
    }

    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        sleep(30000)

        return Vacancy(
            id = UUID.randomUUID(),
            url = url,
            companyName = "TestScrapperSuccessful",
            title = "Senior Java Developer",
            description = "This is a test job description for $url.",
            additionalAttributes = mapOf("location" to "Remote", "level" to "Senior"),
            datePosted = LocalDateTime.now().minusDays(5),
            dateScrapped = LocalDateTime.now(),
            source = getSource(),
            active = true,
            tag = VacancyTag.NEW
        )
    }

    override fun getSource(): VacancySource = VacancySource.TEST_SCRAPPER_SUCCESSFUL
}

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.testScrapperPartlyFailed"],
    havingValue = "true",
    matchIfMissing = false
)
class TestScrapperPartlyFailed(
    vacancyRepository: VacancyRepository,
    scraperJobProgressService: ScraperJobProgressService
) : AbstractVacancyScrapper(vacancyRepository, scraperJobProgressService) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(30000)

        return listOf(
            "https://example.com/TestScrapperPartlyFailed/job1",
            "https://example.com/TestScrapperPartlyFailed/job2",
            "https://example.com/TestScrapperPartlyFailed/job3",
            "https://example.com/TestScrapperPartlyFailed/job4",
            "https://example.com/TestScrapperPartlyFailed/job5",
            "https://example.com/TestScrapperPartlyFailed/job6",
            "https://example.com/TestScrapperPartlyFailed/job7",
            "https://example.com/TestScrapperPartlyFailed/job8",
            "https://example.com/TestScrapperPartlyFailed/job9",
        )
    }

    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        sleep(30000)
        if (
            url == "https://example.com/TestScrapperPartlyFailed/job1" ||
            url == "https://example.com/TestScrapperPartlyFailed/job3" ||
            url == "https://example.com/TestScrapperPartlyFailed/job5" ||
            url == "https://example.com/TestScrapperPartlyFailed/job7" ||
            url == "https://example.com/TestScrapperPartlyFailed/job9"
        ) {
            throw Exception("TestScrapperPartlyFailed Failed")
        }

        return Vacancy(
            id = UUID.randomUUID(),
            url = url,
            companyName = "TestScrapperPartlyFailed",
            title = "Senior Java Developer",
            description = "This is a test job description for $url.",
            additionalAttributes = mapOf("location" to "Remote", "level" to "Senior"),
            datePosted = LocalDateTime.now().minusDays(5),
            dateScrapped = LocalDateTime.now(),
            source = getSource(),
            active = true,
            tag = VacancyTag.NEW
        )
    }

    override fun getSource(): VacancySource = VacancySource.TEST_SCRAPPER_PARTLY_FAILED
}

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.testScrapperFailed"],
    havingValue = "true",
    matchIfMissing = false
)
class TestScrapperFailed(
    vacancyRepository: VacancyRepository,
    scraperJobProgressService: ScraperJobProgressService
) : AbstractVacancyScrapper(vacancyRepository, scraperJobProgressService) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(30000)
        throw Exception("TestScrapperFailed")
    }

    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        return Vacancy(
            id = UUID.randomUUID(),
            url = url,
            companyName = "TestScrapperPartlyFailed",
            title = "Senior Java Developer",
            description = "This is a test job description for $url.",
            additionalAttributes = mapOf("location" to "Remote", "level" to "Senior"),
            datePosted = LocalDateTime.now().minusDays(5),
            dateScrapped = LocalDateTime.now(),
            source = getSource(),
            active = true,
            tag = VacancyTag.NEW
        )
    }

    override fun getSource(): VacancySource = VacancySource.TEST_SCRAPPER_FAILED
}

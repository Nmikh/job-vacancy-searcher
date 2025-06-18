package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.model.VacancyTag
import com.vacancies.searcher.repository.CompanyRepository
import com.vacancies.searcher.repository.VacancyRepository
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
    companyRepository: CompanyRepository
) : AbstractVacancyScrapper(vacancyRepository, companyRepository) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(30000)

        return listOf(
            "https://example.com/TestScrapperSuccessful/job1",
            "https://example.com/TestScrapperSuccessful/job2"
        )
    }

    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        sleep(300)

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
    companyRepository: CompanyRepository
) : AbstractVacancyScrapper(vacancyRepository, companyRepository) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(300)

        return listOf(
            "https://example.com/TestScrapperPartlyFailed/job1",
            "https://example.com/TestScrapperPartlyFailed/job2"
        )
    }

    override fun getVacancy(url: String, parameters: Map<String, List<String>>): Vacancy {
        sleep(300)
        if ("https://example.com/TestScrapperPartlyFailed/job1" == url) {
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
    companyRepository: CompanyRepository
) : AbstractVacancyScrapper(vacancyRepository, companyRepository) {
    override fun getVacancyLinks(parameters: Map<String, List<String>>): List<String> {
        sleep(300)
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

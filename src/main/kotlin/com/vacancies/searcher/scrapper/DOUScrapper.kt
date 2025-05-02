package com.vacancies.searcher.scrapper

import com.vacancies.searcher.model.Vacancy
import com.vacancies.searcher.model.VacancySource
import com.vacancies.searcher.repository.VacancyRepository
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@ConditionalOnProperty(
    value = ["scrapper.providers.dou"],
    havingValue = "true",
    matchIfMissing = false
)
class DOUScrapper(vacancyRepository: VacancyRepository) : AbstractVacancyScrapper(vacancyRepository) {
    companion object {
        private const val BASE_URL = "https://jobs.dou.ua"
        private const val JOB_SEARCH_URL =
            "$BASE_URL/vacancies/?remote&category=Java&exp=5plus"
    }

    override fun getVacancyLinks(parameters: Map<String, String>): List<String> {
        val options = ChromeOptions().apply { addArguments("--headless") }
        return ChromeDriver(options).use { driver ->
            driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS))
            loadAllPages(driver)
            return@use extractVacancyLinks(driver)
        }
    }

    private fun WebDriver.use(scrapping: (WebDriver) -> List<String>): List<String> {
        return try {
            scrapping(this)
        } finally {
            quit()
        }
    }

    private fun loadAllPages(driver: WebDriver) {
        driver[JOB_SEARCH_URL]
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
        generateSequence { findMoreButton(driver) }.forEach { it.click(); sleep(5000) }
    }

    private fun findMoreButton(driver: WebDriver): WebElement? =
        driver.findElements(By.className("more-btn")).firstOrNull { it.text.isNotBlank() }

    private fun extractVacancyLinks(driver: WebDriver): List<String> =
        driver.findElements(By.cssSelector("a.vt")).mapNotNull { it.getAttribute("href") }

    override fun getVacancy(url: String, parameters: Map<String, String>): Vacancy {
        sleep(30000)

        val document = Jsoup.connect(url).timeout(10 * 1000).get()
        val company = document.select(".l-n a").firstOrNull()?.text()?.trim().toString()
        val description =
            document.selectFirst(".vacancy-section")?.children()?.joinToString("\n") { it.text() }.toString()
        val ukDate = document.selectFirst(".date")?.text()?.trim().toString()

        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("uk"))
        val datePosted = LocalDate.parse(ukDate, formatter)

        return Vacancy(
            id = UUID.randomUUID(),
            url = url,
            company = company,
            title = document.title(),
            description = description,
            datePosted = datePosted.atStartOfDay(),
            dateScrapped = LocalDateTime.now(ZoneOffset.UTC),
            source = VacancySource.DOU,
            active = true
        )
    }

    override fun getSource(): VacancySource = VacancySource.DOU
}

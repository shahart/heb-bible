package edu.hebbible;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class SeleniumIntegrationTest {

    static WebDriver driver;

    @BeforeAll
    static void setup() throws Exception {
        HebBible.main(new String[] {}); // TODO consider test-containers
        Thread.sleep(15_000); // time for spring-boot to be up And running
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
        Thread.sleep(5_000);
    }

    @Test
    void psukimTotal() {
        WebElement count = driver.findElement(By.className("count"));
        Assertions.assertTrue(count.getText().contains("23203"), count.getText());
        driver.quit();
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

}


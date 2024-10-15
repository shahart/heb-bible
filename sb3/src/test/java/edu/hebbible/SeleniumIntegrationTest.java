package edu.hebbible;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().startsWith('windows')}", reason = "this os.name is not Linux, which got DevToolsActivePort file doesn't exist")
class SeleniumIntegrationTest { // todo with Profile so it will work on my local, but not on github server

    static WebDriver driver;

    @BeforeAll
    static void setup() throws Exception {
        HebBible.main(new String[]{}); // TODO consider test-containers
        Thread.sleep(15_000); // time for spring-boot to be up And running

        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
        Thread.sleep(5_000);
    }

    @Test
    void psukimTotal() {
        WebElement count = driver.findElement(By.className("count"));
        Assertions.assertTrue(count.getText().contains("23203"), count.getText());
    }

    @Test
    void pasukByName() throws Exception {
        WebElement text = driver.findElement(By.id("text"));
        text.sendKeys("שחר");
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();
        Thread.sleep(3_000);
        WebElement result = driver.findElement(By.className("result"));
        Assertions.assertTrue(result.getText().contains("25"), result.getText());
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

}


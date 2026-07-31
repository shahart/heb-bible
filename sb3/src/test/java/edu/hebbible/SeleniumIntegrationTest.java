package edu.hebbible;

import edu.hebbible.persistence.UsageRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@EnabledOnOs({OS.WINDOWS, OS.MAC, OS.LINUX})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumIntegrationTest {

    @Value("${local.server.port}")
    int port;
    @MockitoBean
    UsageRepository usageRepository;
    static WebDriver driver;
    WebDriverWait wait;

    @BeforeAll
    static void setup() {
        var paths = SeleniumManager.getInstance()
                .getBinaryPaths(List.of("--browser", "chrome", "--skip-driver-in-path"));
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(paths.getDriverPath()))
                .build();
        ChromeOptions options = new ChromeOptions()
                .setBinary(paths.getBrowserPath())
                .addArguments("--headless=new");
        driver = new ChromeDriver(service, options);
    }

    @BeforeEach
    void openPage() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:" + port);
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear()");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        driver.findElement(By.id("email")).sendKeys("selenium-" + UUID.randomUUID() + "@example.com");
        driver.findElement(By.id("password")).sendKeys("valid-password");
        driver.findElement(By.cssSelector("button[onclick='signup()']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user")));
    }

    @Test
    void psukimTotal() {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("count"), "23204"));
        WebElement count = driver.findElement(By.className("count"));
        Assertions.assertTrue(count.getText().contains("23204"), count.getText());
    }

    @Test
    void pasukByName() {
        WebElement text = driver.findElement(By.id("text"));
        text.sendKeys("שחר");
        WebElement submitButton = driver.findElement(By.cssSelector("button[onclick='pasuk()']"));
        submitButton.click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("result"), "25"));
        WebElement result = driver.findElement(By.className("result"));
        Assertions.assertTrue(result.getText().contains("25"), result.getText());
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}

package edu.hebbible;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledOnOs({OS.WINDOWS, OS.MAC, OS.LINUX})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayWrightIntegrationWebkitTest {

    @Value("${local.server.port}")
    int port;
    static Playwright playwright;
    static Browser browser;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void openPage() {
        page = browser.newPage();
        page.navigate("http://localhost:" + port);
        signUp();
    }

    @Test
    void psukimTotal() {
        assertThat(page.locator(".count")).containsText("23204");
    }

    @Test
    void pasukByName() {
        // todo
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @AfterEach
    void closePage() {
        page.close();
    }

    private void signUp() {
        page.getByPlaceholder("email").fill("playwright-webkit-" + UUID.randomUUID() + "@example.com");
        page.getByPlaceholder("password").fill("valid-password");
        page.getByText("Sign up", new Page.GetByTextOptions().setExact(true)).click();
    }
}

package edu.hebbible;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().startsWith('windows')}", reason = "this os.name is not Linux, which got DevToolsActivePort file doesn't exist")
class PlayWrightIntegrationFirefoxTest { // todo with Profile so it will work on my local, but not on github server

    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeAll
    static void launchBrowser() throws Exception { // setUp
        HebBible.main(new String[]{}); // TODO consider test-containers
        Thread.sleep(15_000); // time for spring-boot to be up And running

        playwright = Playwright.create();
        browser = playwright.firefox().launch((new BrowserType.LaunchOptions().setHeadless(false))); // nightly
        page = browser.newPage();
        page.navigate("http://localhost:8080");
        Thread.sleep(5_000);
    }

    @Test
    void psukimTotal() {
        assertThat(page.locator(".count")).containsText("23204");
    }

    @Test
    void pasukByName() throws Exception {
        // todo
    }

    @AfterAll
    static void closeBrowser() { // tearDown
        playwright.close();
    }

}


package edu.hebbible;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;
//import org.springframework.test.context.junit.jupiter.EnabledIf;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// @EnabledIf(expression = "#{systemProperties['os.name'].toLowerCase().startsWith('windows')}", reason = "this os.name is not Linux, which got DevToolsActivePort file doesn't exist")
class PlayWrightIntegrationTest { // todo with Profile so it will work on my local, but not on github server

    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeAll
    static void launchBrowser() throws Exception { // setUp
        HebBible.main(new String[]{}); // TODO consider test-containers
        Thread.sleep(15_000); // time for spring-boot to be up And running

        playwright = Playwright.create();
        browser = playwright.chromium().launch(); // headless true. build: Latest 131
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


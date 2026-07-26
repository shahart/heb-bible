package edu.hebbible;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayWrightIntegrationTest {

    @Value("${local.server.port}")
    int port;
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    void openPage() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate(baseUrl());
    }

    @Test
    void anonymousUserSeesLoginFlow() {
        Assertions.assertTrue(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Sign in with Google")).isVisible());
        Assertions.assertTrue(page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Sign up")).isVisible());
    }

    @Test
    void emailSignupSurvivesRefreshAndHasOneWorkingLogoutButton() {
        String email = "playwright-" + UUID.randomUUID() + "@example.com";

        page.getByPlaceholder("email").fill(email);
        page.getByPlaceholder("password").fill("valid-password");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign up")).click();

        Locator user = page.locator(".user");
        user.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        Assertions.assertEquals("Signed in as " + email, user.textContent());
        Assertions.assertEquals(1, page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Logout").setExact(true)).count());

        page.reload();
        user.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        Assertions.assertEquals("Signed in as " + email, user.textContent());

        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Logout").setExact(true)).click();
        page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Sign up"))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        Assertions.assertEquals(0, page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Logout").setExact(true)).count());
        Assertions.assertNull(page.evaluate("window.localStorage.getItem('jwtToken')"));
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/";
    }
}

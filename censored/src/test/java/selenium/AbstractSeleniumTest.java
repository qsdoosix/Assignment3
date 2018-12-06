package selenium;

import ca.carleton.poker.PokerApplication;
import config.MockUserFactory;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.concurrent.TimeUnit;

/**
 * Parent test for all selenium classes so we can wait for links.
 * <p/>
 * Created by Mike on 10/6/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PokerApplication.class, locations = {"/META-INF/applicationContext.xml"})
@WebIntegrationTest(value = "server.port:8080")
public abstract class AbstractSeleniumTest {

    @Autowired
    protected WebDriver webDriver;

    @Autowired
    private MockUserFactory mockUserFactory;

    /**
     * Wait the specified number of seconds (sleeps the test).
     *
     * @param seconds the number of seconds.
     */
    public void delay(final int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (final Exception ignored) {
        }
    }

    /**
     * Connect a second user.
     *
     * @return the user.
     */
    public ChromeDriver quickConnectAnotherUser() {
        // Lets connect a second player
        final ChromeDriver user = this.mockUserFactory.getAnotherUser("http://localhost:8080");
        user.findElement(By.id("connect")).click();
        return user;
    }

    public void disconnectSecondUser(final ChromeDriver user) {
        user.findElement(By.id("disconnect")).click();
        user.quit();
    }

    /**
     * Wait for an element to become displayed (max 3 seconds).
     *
     * @param id the element's id.
     * @return the element.
     */
    public WebElement waitForDisplayed(final String id) {
        return new WebDriverWait(this.webDriver, 5).until(visibilityOf(this.webDriver.findElement(By.id(id))));
    }

    /**
     * Wait for an element to become displayed (max 5 seconds).
     *
     * @param element the element
     * @return the element.
     */
    public WebElement waitForDisplayed(final WebElement element) {
        return new WebDriverWait(this.webDriver, 5).until(visibilityOf(element));
    }

    /**
     * Wait for an element to become hidden.
     *
     * @param element the element
     */
    public void waitForHidden(final WebElement element) {
        new WebDriverWait(this.webDriver, 5).until(not(visibilityOf(element)));
    }
    
    
    public void waitFor(int i) {
    	new WebDriverWait(this.webDriver, i).withTimeout(i, TimeUnit.SECONDS);
		
	}


	public void waitForAlert() {
		new WebDriverWait(this.webDriver,5).until(ExpectedConditions.alertIsPresent());

	}

	public void waitForText(String searchKey) {
		new WebDriverWait(this.webDriver,5).until(visibilityOf(this.webDriver.findElement(By.xpath("//*[contains(text(),'" + searchKey + "')]"))));
	}
	
	

}



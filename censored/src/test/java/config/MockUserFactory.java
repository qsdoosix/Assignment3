package config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Mock a user (aka a second browser).
 * <p/>
 * Created by Mike on 11/8/2015.
 */
@Service
public class MockUserFactory {

    public ChromeDriver getAnotherUser(final String location) {
        return (ChromeDriver) this.buildNewUser(location);
    }

    private WebDriver buildNewUser(final String location) {
        final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setJavascriptEnabled(true);

        final ChromeDriver chrome = new ChromeDriver(capabilities);
        chrome.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        chrome.get(location);
        return chrome;
    }
}

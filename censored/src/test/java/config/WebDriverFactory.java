package config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * created by Cheryl 
 */
public class WebDriverFactory {

    public WebDriverFactory() {}

    public WebDriver getWebDriver() {
        final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setJavascriptEnabled(true);
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");
        

        final ChromeDriver chrome = new ChromeDriver(capabilities);
        chrome.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        return chrome;
    }
}

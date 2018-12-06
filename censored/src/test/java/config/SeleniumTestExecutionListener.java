package config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Code to help with selenium integration tests. Inject a selenium test ca.carleton.blackjack.driver into the class.
 * <p/>
 * Taken from:
 * <p/>
 * http://www.javacodegeeks.com/2015/03/spring-boot-integration-testing-with-selenium.html
 * <p/>
 * Created by Mike on 10/6/2015.
 */
public class SeleniumTestExecutionListener extends AbstractTestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumTestExecutionListener.class);

    private ChromeDriver webDriver;

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void prepareTestInstance(final TestContext testContext) throws Exception {
        if (this.webDriver != null) {
            return;
        }
        final ApplicationContext context = testContext.getApplicationContext();
        if (context instanceof ConfigurableApplicationContext) {
            this.webDriver = (ChromeDriver) context.getBean("webDriver");
            final ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
            final ConfigurableListableBeanFactory bf = configurableApplicationContext.getBeanFactory();
            bf.registerResolvableDependency(WebDriver.class, this.webDriver);
            LOG.info("Set web driver in text execution listener.");
        }
    }

    @Override
    public void beforeTestMethod(final TestContext testContext) throws Exception {
        if (this.webDriver != null) {
            final SeleniumTest annotation = findAnnotation(
                    testContext.getTestClass(), SeleniumTest.class);
            this.webDriver.get(annotation.baseUrl());
        }
    }
  

}
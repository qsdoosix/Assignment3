package selenium;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;

import config.SeleniumTest;
import selenium.page.IndexPage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cheryl 
 */
@SeleniumTest
public class GameTest extends AbstractSeleniumTest {

    @Autowired
    private IndexPage indexPage;
    
 

    @Test
    public void processAI(){
    	//connect
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.indexPage.open.click();
    	this.indexPage.start.click();
    
    	assertThat(this.waitForDisplayed(this.indexPage.stay).isEnabled(), is(true));
        assertThat(this.indexPage.hasText("All AI players have made their choices"), is(true));
 
        //disconnect
        this.indexPage.disconnect.click();        
    }
 
    

 
	@Test
    public void canUseStayOption(){
		//connect
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.indexPage.open.click();
    	this.indexPage.start.click();
    
    	// checks that the use can choose to stay
    	this.waitForDisplayed(this.indexPage.stay).isEnabled();
        this.indexPage.stay.click();
        assertThat(this.indexPage.hasText("You decided to STAY"), is(true));
        
      //disconnect
     	this.indexPage.disconnect.click();
    }


    @Test
    public void canUseHitOption() {
    	//connect
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.indexPage.open.click();
    	this.indexPage.start.click();
    	
    	// checks that the use can choose to hit
    	this.waitForDisplayed(this.indexPage.hit).isEnabled();
        this.indexPage.hit.click();
        assertThat(this.indexPage.hasText("You decided to HIT"), is(true));
    
        
        //disconnect
       this.indexPage.disconnect.click();
    }
    
    
 
    @Test
    public void canImproveCards(){
    	//connect
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.indexPage.open.click();
    	this.indexPage.start.click();
    	// checks that the use can choose to stay
    	
    	this.waitForDisplayed(this.indexPage.hit).isEnabled();
        this.indexPage.hit.click();
        this.waitForDisplayed(this.indexPage.done).isEnabled();
        this.indexPage.card1.click();
        this.indexPage.card3.click();
        this.indexPage.done.click();
        assertThat(this.indexPage.hasText("You decided to improve card"), is(true));
        
        //disconnect
        this.indexPage.disconnect.click();
    }
    
    
   
  
    
    
    
    
}

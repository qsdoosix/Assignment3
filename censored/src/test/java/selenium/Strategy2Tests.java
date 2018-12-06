package selenium;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;

import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.Hand;
import ca.carleton.poker.game.entity.card.Rank;
import ca.carleton.poker.game.entity.card.Suit;
import config.SeleniumTest;
import selenium.page.IndexPage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cheryl 
 */
@SeleniumTest
public class Strategy2Tests extends AbstractSeleniumTest {

    @Autowired
    private IndexPage indexPage;
    
    
    @Test
    public void Rig1(){
    	/* This had is rigged with :
    	 * 	- O1:  one pair
    	 *  - O2: one pair
    	 *  - 03: two pairs
    	 *  - p:  two pairs
    	 */
    	
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.delay(2);
    	this.indexPage.open.click(); // defaults is 1 player
    	this.waitForDisplayed(this.indexPage.rig).isEnabled();
    	this.indexPage.rig.click();
    	
    	/************************* Set inital hands ************************/
    	// Player  straight
    	this.waitForAlert();
    	Alert a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-2 hearts, rank-2 spades, rank-4 clubs, rank-4 spades, rank-k clubs");
    	a.accept();
    	
    	// other 1  one pair
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-10 hearts, rank-10 clubs, rank-7 hearts, rank-2 hearts, rank-a hearts");
    	a.accept();
    	
    	// other 2 one pair
    	this.waitForAlert();
    	a =this.webDriver.switchTo().alert();
    	a.sendKeys("rank-6 clubs, rank-6 hearts, rank-9 spades, rank-2 spades, rank-7 diams");
    	a.accept();
    	
    	
    	// other 3 has 2 pair
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-2 diams, rank-2 clubs, rank-j diams, rank-j clubs, rank-6 diams");
    	a.accept();
     

    	assertThat(this.indexPage.hasText("Setting all cards"), is(true));     
    	/****************************** check user options ************************/
    	
    	String[] ids = this.indexPage.getUsetTexts();
    	// user 1 will HIT 
    	
    	//Handle prompt to improve user 1  - gets nothing
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("0:rank-a clubs, 1:rank-k spades, 4:rank-9 diams");
    	a.accept();
    	
    	String result = ids[0] + " choose to HIT";
    	System.out.println(this.indexPage.hasText(result));
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	this.delay(5);
    	//user 2 has hit
    	//Handle prompt to improve user 2 - gets a a full house
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("0:rank-3 clubs, 3:rank-3 spades, 4:rank-3 diams");
    	a.accept();
    	
 
    	result = ids[1] + " choose to HIT";
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	//user 3 has hit
    	//Handle prompt to improve user 3  - gets full house

    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("2:rank-j spades");
    	a.accept();
    	
    	this.delay(1);
    	result = ids[2] + " choose to HIT";
    	assertThat(this.indexPage.hasText(result), is(true));
    	    	
    	// Player will stay
    	this.delay(1);
     	assertThat(this.waitForDisplayed(this.indexPage.stay).isEnabled(), is(true));
    	this.indexPage.stay.click();
    	
    	this.waitForDisplayed(this.indexPage.start);
    
    	/*****************  Check results ******************/
    	delay(1);
    	// user 1 wins
    	
    	result = ids[0].trim() + " lost with a score of ONE_PAIR, ranked 4";
    	assertThat(this.indexPage.hasText(result), is(true));
    	//user 2 looses
    	result = ids[1].trim() + " lost with a score of FULL_HOUSE, ranked 2";
    	assertThat(this.indexPage.hasText(result), is(true));
    
    	// user 3 looses
    	result = ids[2].trim() + " won with a score of FULL_HOUSE, ranked 1";
    	assertThat(this.indexPage.hasText(result), is(true));
    
    	// Player looses
    	result = ids[3].trim() + " lost with a score of TWO_PAIR, ranked 3";
    	assertThat(this.indexPage.hasText(result), is(true));
    
    	
    	this.delay(5);
    	this.indexPage.disconnect.click();
    
    }


    @Test
    public void Rig2(){
    	/*This had is rigged with :
       	 * 	- O1:  has HIGH_CARD
       	 *  - O2:  has HIGH_Card
       	 *  - 03:  has 1 pair
       	 *  - p:   has flush
       	 */
    	
    	this.indexPage.connect.click();
    	this.waitForDisplayed(this.indexPage.open).isEnabled();
    	this.delay(2);
    	this.indexPage.open.click(); // defaults is 1 player
    	this.waitForDisplayed(this.indexPage.rig).isEnabled();
    	this.indexPage.rig.click();
    	/************************* Set inital hands ************************/
    	// Player  straight
    	this.waitForAlert();
    	Alert a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-2 hearts, rank-3 hearts, rank-4 hearts, rank-5 clubs, rank-6 hearts");
    	a.accept();
    	
    	// other 1 nothing
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-10 hearts, rank-5 clubs, rank-3 diams, rank-9 spades, rank-a hearts");
    	a.accept();
    	
    	// other 2 nothing
    	this.waitForAlert();
    	a =this.webDriver.switchTo().alert();
    	a.sendKeys("rank-j clubs, rank-j spades, rank-8 clubs, rank-2 spades, rank-3 diams");
    	a.accept();
    	
    	
    	// other 3 one pair
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("rank-2 clubs, rank-9 spades, rank-q clubs, rank-q spades, rank-4 diams");
    	a.accept();
     

    	assertThat(this.indexPage.hasText("Setting all cards"), is(true)); 
    	this.delay(5);
    	/****************************** check user options ************************/
    	
    	//Handle prompt to improve user 1  gets a full house
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("0:rank-5 clubs,1:rank-5 diams,2:rank-3 spades, 3:rank-3 hearts, 4:rank-5 spades");
    	a.accept();
    	
    	
    	String[] ids = this.indexPage.getUsetTexts();
    	// user 1 will Hit 
    	String result = ids[0] + " choose to HIT";
    	System.out.println(this.indexPage.hasText(result));
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	
    	//Handle prompt to improve user 2  gets nothing
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("0:rank-10 hearts, 1:rank-j hearts, 2:rank-q hearts, 3:rank-k hearts, 4:rank-a hearts");
    	a.accept();
    	
    	//user 2 has Hit
    	this.delay(1);
    	result = ids[1] + " choose to HIT";
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	// user 3 will hit
    	//Handle prompt to improve user 3 gets nothing
    	this.waitForAlert();
    	a = this.webDriver.switchTo().alert();
    	a.sendKeys("0:rank-2 clubs,1:rank-2 diams,4:rank-j diams");
    	a.accept();
    	
    	this.delay(1);
    	result = ids[2] + " choose to HIT";
    	assertThat(this.indexPage.hasText(result), is(true));
    	    	
    	// Player will stay
    	this.delay(5);
     	assertThat(this.waitForDisplayed(this.indexPage.stay).isEnabled(), is(true));
    	this.indexPage.stay.click();

    	this.delay(2);
    	assertThat(this.waitForDisplayed(this.indexPage.start).isEnabled(), is(true));
    	/*****************  Check results ******************/
    	this.delay(5);
    	// user 1 loses
    	result = ids[0] + " lost with a score of FULL_HOUSE, ranked 2";
    	System.out.println(this.indexPage.hasText(result));
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	//user 2 looses
    	result = ids[1] + " won with a score of ROYAL_FLUSH, ranked 1";
    	assertThat(this.indexPage.hasText(result), is(true));
    	
    	// user 3 looses
    	result = ids[2] + " lost with a score of ONE_PAIR, ranked 4";
    	assertThat(this.indexPage.hasText(result), is(true));
    	    	
    	// Player looses
    	result = ids[3] + " lost with a score of STRAIGHT, ranked 3";
    	assertThat(this.indexPage.hasText(result), is(true));
    
    	this.delay(5);
    	
    	this.indexPage.disconnect.click();
    
    }
    
    
   




 
 
 

  
    
    
    
    
}

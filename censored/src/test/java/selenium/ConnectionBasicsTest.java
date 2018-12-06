package selenium;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import config.SeleniumTest;
import selenium.page.IndexPage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

/**
 * Test the ability to connect and disconnect to the game.
 * Created by Cheryl 
 */
@SeleniumTest
public class ConnectionBasicsTest extends AbstractSeleniumTest{

    @Autowired
    private IndexPage indexPage;
    
    @Test
    public void canConnect() {
    
    	this.indexPage.connect.click();
        assertThat(this.indexPage.hasText("Successfully connected to the game with unique "), is(true));
        assertThat(this.indexPage.hasText("You have been designated the admin for this game."), is(true));
    
        this.indexPage.disconnect.click();
        assertThat(this.indexPage.hasText("Connection closed"), is(true));
    
        
    }
    

    @Test
    public void canOpenLobby() {
    	this.indexPage.connect.click();
    	this.indexPage.open.click();
    	assertThat(this.indexPage.hasText("Opening the lobby with specified settings."), is(true));
    	 this.indexPage.disconnect.click(); 
    }
 
    @Test
    public void canConenctTwoPlayers() {
    	// connect
     	this.indexPage.connect.click();
    	this.indexPage.setNumberPlayers(2);
    	this.indexPage.open.click();
    	
    	//second connect
    	ChromeDriver second = (ChromeDriver) this.quickConnectAnotherUser();
    	assertThat(this.indexPage.hasText("Successfully connected to the game with unique id"), is(true));
    	assertThat(this.indexPage.start.isEnabled(), is(true));
    	
    	// quit
    	this.disconnectSecondUser(second);
    	this.indexPage.disconnect.click();
     	
    	
    	
    }
    
    @Test
    public void canConenctMultiplePlayers() {
    	// connect 
    	this.indexPage.connect.click();
     	this.indexPage.setNumberPlayers(3);
     	this.indexPage.open.click();
     	
    	// connect second
    	ChromeDriver second = (ChromeDriver) this.quickConnectAnotherUser();
    	assertThat(this.indexPage.hasText("Successfully connected to the game with unique id "), is(true));
    
    	// connect third
    	ChromeDriver third = (ChromeDriver) this.quickConnectAnotherUser();
    	assertThat(this.indexPage.hasText("Successfully connected to the game with unique id "), is(true));
   
    	this.disconnectSecondUser(second);
    	this.disconnectSecondUser(third);
    	this.indexPage.disconnect.click();  
    }
    
    @Test
    public void canStartGame() {
    	this.indexPage.connect.click();
    	this.indexPage.open.click();
    	this.indexPage.start.click();
    	assertThat(this.indexPage.hasText("The game has started! Please wait for your turn."), is(true));
    	this.indexPage.disconnect.click();
    }
    
   
    
}

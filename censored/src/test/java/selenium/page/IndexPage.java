package selenium.page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ca.carleton.poker.game.PokerGame;
import ca.carleton.poker.game.entity.card.Hand;

import org.apache.commons.lang3.StringUtils;
import selenium.page.IndexPage;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Lazy
@Component
public class IndexPage extends  AbstractPage<IndexPage> {

	
	@Autowired
	public PokerGame game; 
	 
  //  public ChromeDriver chrome;
    
    @FindBy(id = "connect")
    public WebElement connect;

    @FindBy(id = "disconnect")
    public WebElement disconnect;

    @FindBy(id = "numberPlayers")
    public WebElement numberPlayers;

    @FindBy(id = "open")
    public WebElement open;
    
    @FindBy(id = "rig")
    public WebElement rig;

    @FindBy(id = "start")
    public WebElement start;

    @FindBy(id = "stay")
    public WebElement stay;

    @FindBy(id = "hit")
    public WebElement hit;
    
    @FindBy(id ="done")
    public WebElement done;

    @FindBy(id = "consoleText")
    public WebElement consoleText;
    
    @FindBy(id = "playerHandCards")
    public WebElement playerHandCards;
    
    @FindBy(id = "PlayerCard1")
    public WebElement card1;
    @FindBy(id = "PlayerCard2")
    public WebElement card2;
    @FindBy(id = "PlayerCard3")
    public WebElement card3;
    @FindBy(id = "PlayerCard4")
    public WebElement card4;
    @FindBy(id = "PlayerCard5")
    public WebElement card5;
    
    
    @FindBy(id = "otherHandCards1")
    public WebElement otherPlayer1Cards;
    
    @FindBy(id = "otherHand1Card0")
    public WebElement otherHand1Card0;
    
    @FindBy(id = "otherHand1Card1")
    public WebElement otherHand1Card1;
    
    @FindBy(id = "otherHand1Card2")
    public WebElement otherHand1Card2;
    
    @FindBy(id = "otherHand1Card3")
    public WebElement otherHand1Card3;
    
    @FindBy(id = "otherHand1Card4")
    public WebElement otherHand1Card4;
    
    @FindBy(id = "otherHandCards2")
    public WebElement otherPlayer2Cards;
    
    @FindBy(id = "otherHand2Card0")
    public WebElement otherHand2Card0;
    
    @FindBy(id = "otherHand2Card1")
    public WebElement otherHand2Card1;
    
    @FindBy(id = "otherHand2Card2")
    public WebElement otherHand2Card2;
    
    @FindBy(id = "otherHand2Card3")
    public WebElement otherHand2Card3;
    
    @FindBy(id = "otherHand2Card4")
    public WebElement otherHand2Card4;
    
    @FindBy(id = "otherHandCards3")
    public WebElement otherPlayer3Cards;
    
    @FindBy(id = "otherHand3Card0")
    public WebElement otherHand3Card0;
    
    @FindBy(id = "otherHand3Card1")
    public WebElement otherHand3Card1;
    
    @FindBy(id = "otherHand3Card2")
    public WebElement otherHand3Card2;
    
    @FindBy(id = "otherHand3Card3")
    public WebElement otherHand3Card3;
    
    @FindBy(id = "otherHand3Card4")
    public WebElement otherHand3Card4;

    @FindBy(id = "console")
    public WebElement console;
	 
    
 
    
    public int countNumberOfCardsFor(final WebElement cardList) {
        return this.getAllCardsFor(cardList).size();
    }
   
    /**
     * Fetch all the inner nodes of the given web element.
     *
     * @param cardList the list.
     * @return the list of 'li' elements.
     */
    public List<WebElement> getAllCardsFor(final WebElement cardList) {
    	return this.webDriver.findElements(By.xpath(String.format("//ul[@id='%s']/li", cardList.getAttribute("id"))));
   }
    

    
  
    /**
     * Return the player's UID if connected.
     *
     * @return the UID.
     */
    public String getPlayerUID() {
    	
    	if (!this.connect.isEnabled()) {
            final String consoleText = this.consoleText.getText();
            return consoleText.replace("Console (UID: ", "").replace(")", "").trim();
        }
       return null;
   }


    /**
     * Return the other player's UID if connected.
     *
     * @return the UID.
     */
    public String[] getUsetTexts() {
    	String[] ids = new String[4];
    	ids[0] = this.webDriver.findElement(By.id("otherHandText1")).getText().replace("Other Player's Hand", "").replaceAll("[\\(\\)]", "");
    	ids[1] = this.webDriver.findElement(By.id("otherHandText2")).getText().replace("Other Player's Hand", "").replaceAll("[\\(\\)]", "");
    	ids[2] = this.webDriver.findElement(By.id("otherHandText3")).getText().replace("Other Player's Hand", "").replaceAll("[\\(\\)]", "");
    	ids[3] = this.webDriver.findElement(By.id("yourHandText")).getText().replace("Your Hand", "").replaceAll("[\\(\\)]", "");
        
    	return ids;
   }
    /**
     * Set the number of players.
     *
     * @param number the number of players.
     */
    public void setNumberPlayers(final int number) {
        this.webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        if (this.webDriver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) this.webDriver).executeScript(
                    String.format("document.getElementById('numberPlayers').setAttribute('value', '%s')", number));
        }
        this.webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }
    
    
    public void setInnerHTML(String[] s, WebElement e){
    	String results = "";
    	for (int i = 0; i < s.length; i++) {
    		results += s[i];
    	}
    
    		
    	// add results to values
    	((JavascriptExecutor)this.webDriver).executeScript(
   			 "arguments[0].innerHTML = arguments[1];", e, results);
    	// add results to values
    	((JavascriptExecutor)this.webDriver).executeScript(
   			 "arguments[0].innerHTML = arguments[1];", e, results);
    	
    				
    }
    	



    protected String getPageName() {
       return StringUtils.EMPTY;
    }



	public void updateCards() {
		WebElement e = this.otherPlayer1Cards;
	
		// add submit button 
    	((JavascriptExecutor)this.webDriver).executeScript(
    			"arguments[0].onclick = sendAllCards();",  e);
    
    	// submit form 
    	
    	((JavascriptExecutor)this.webDriver).executeScript(
    			"arguments[0].click();", e);
	}
	
	
    public void getWindow(ChromeDriver window){
    	((JavascriptExecutor) window).executeScript("window.focus();");

    }



	

}
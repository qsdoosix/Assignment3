# 4004-SeleniumPoker
A basic Poker web application to get experience using selenium as a testing method.

Some code taken from the web (spring documentation), see:

https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-websocket-tomcat

Acknowledgments 
Some of this code was taken from Mike's blackjack game: https://github.com/Sacredify/4004-SeleniumBlackjack.git

To build this project
---------------------

Building this project requires git (or download it manually), and maven. 

  1. `git clone https://github.com/CD11/SeleniumPoker`
  2. `cd SelemiumPoker`
  3. `mvn clean install`
  

IMPORTING AND RUNNING WITH ECLIPSE (Eclipse NEON)
----------------------

For the eclipse users, you can:

  1. downloand Selenium poker from the zip or githu clone
  2. `File --> Import --> Maven --> Import existing maven projects --> SeleniumPoker --> pom.xml selected.
  3. From there, you can do whatever. Select the main class (PokerApplication) and run (as application).



Running the tests
-----------------

Tests can be found in /src/test/java/selenium, right click the pacakge and run as junit test, test are as follows
  1. Connections Based test -  tests that a user can connect to the game, open the lobby, start the game, disconnect
  2. Game test- tests that the use can all of the game options
  3. Ranking test - checking proper ranking of the possible hands: (1 human player):
     * rig their hands a first time with 4 different hands and check ranking
     * rig their hands a 2nd time with 4 other hands and check ranking
     * rig their hands a 3rd time with the 2 other hands not yet tested and two don’t cares and check ranking
  4. Strategy 1 tests
     * define a 4 players game with 3 AI players:
       * player 1: has straight or better, holds
       * player 2: has 3 of a kind: exchanges two others, gets nothing
       * player 3: has a pair: exchanges 3 others, gets nothing
       * player 4: human… don’t care
     * define a 4 players game with 3 AI players:
       * player 1: has 2 pairs, exchanges and gets full house
       * player 2: has 2 pairs, exchanges, gets nothing
       * player 3: has 3 of a kind, exchanges, gets full house
       * player 4: human… don’t care
  5. Strategy 2 tests
     * define a 4 players game with 3 AI players:
       * player 1: has a pair: reuses Strategy 1, exchanges 3 others, gets nothing (so the 3 visible cards are NOT of the same kind)
       * player 2: has a pair: reuses Strategy 1, exchanges 3 others, gets full house  (so the 3 visible cards ARE of the same kind)
       * player 3: has two pairs: exchanges 1 card, gets a full house that beats the one of player 2
       * player 4: human… don’t care
     * define a 4 players game with 3 AI players:
       * player 1: has nothing: reuses Strategy 1, exchanges 5 cards, gets full house(so 3 of the visible cards ARE of the same kind)
       * player 2: has nothing: exchanges 5 cards, gets a royal flush (so the 3 visible cards ARE NOT of the same kind)
       * player 3: has one pair: exchanges 3 cards, gets nothing
       * player 4: human… don’t care

How To Play the Game
---------------------

  1. Connect to the game
     * if you are the first person to connect you will be assigned as admin
     * As admin you can set the number of players and open the loby 
  2. Once enough people have connected you can start the game
  3. All UI Players will play first
  4. When it is your turn the game option buttons will be enabled and you can decide to stay or hit. 
     * If you hit you need to click on cards to improve them.
     * When you have clicked all cards you want to imrpove click done.
     * Note:  Once you click a card you cannot unselect it.
  5. Once Each player has had one turn the system will display the winner and you can disconnect or start the game again.
  
   ## AI Players
   AI Players play using a Strategy design pattern where: 
   1. Strategy 1: 
      * if this AI player has a straight or better, hold (ie do not exchange any card)
      * else this AI player attempts to get a full house by exchanging everything that is not a pair or 3 of a kind
   2. Strategy 2:  
      * IF you are the first player to play, use Option 1 Strategy 1
      * ELSE IF any player before you has 3 visible cards of the same kind: keep your pair(s) if any, and exchange other cards
        * ELSE use  Option 1 Strategy 1
        
   *NOTE: to See what strategy is used you can check the console Log. 


Rigging the Game
-----------------
  1. Connect to the game 
  2. once enough people are connected press rigged_game
  3. the admin will  receive 5 prompts to set all the hands
     * to set a hand type (rank-? suit, rank-? suit, rank-? suit, rank-? suit)
     * each card must be separated by comma
        * the ? represents the rank value where:
           * a = ace
           * k = king
           * q = queen
           * j = jack
           * everything else is its number
        * suits can be:
          * hearts
          * diams
          * spades
          * clubs
   
  4. Once all the players have a card 
     * AI play first, if the AI chooses to hit the admin will be prompted to set which cards to improve
     * Players have a choose to stay or hit, if they hit, press done and you the player will be prompted to set which cards to improve
     * Improving the cards
        * to improve the cards type (index:rank-? suit, )
        * index is the position of the card you want to replace (All cards are sorted by rank)
        * rank and suit are the same as setting the hand;
        * each card must be separated by a comma
  5. once all players have played the results will be displayed
  
  
  
  Winning the Game
  ------------------
  
  Poker hand Ranks are as follows:
   1. Royal Flush
   2. Striaght Flush
   3. Four of a kind
   4. Full House
   5. Flush
   6. Straight
   7. Three of a kind
   8. Two Pair
   9. One Pair
   10. High Card
   ** if two players have the same poker hand the player with the highest card in their whole hand wins
   


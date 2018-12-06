package ca.carleton.poker.game;

import ca.carleton.poker.game.Player.AIPlayer;
import ca.carleton.poker.game.Player.Player;
import ca.carleton.poker.game.Player.RealPlayer;
import ca.carleton.poker.game.PokerGame.State;
import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.HandStatus;
import ca.carleton.poker.game.entity.card.PokerHand;
import ca.carleton.poker.game.entity.card.Rank;
import ca.carleton.poker.game.message.MessageUtil;
import ca.carleton.poker.strategy.AIService;
import ca.carleton.poker.strategy.AIStrategy1;

import org.jetbrains.annotations.NotNull;
import org.seleniumhq.jetty7.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static ca.carleton.poker.game.message.MessageUtil.message;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.size;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.containsAny;

/**
 * Model the Poker Game
 * 
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PokerGame {

    private static final Logger LOG = LoggerFactory.getLogger(PokerGame.class);

    private static final int DEFAULT_MAX_PLAYERS = 4; 
  
    private final AtomicInteger counter = new AtomicInteger(1243512);

    private int roundMaxPlayers = -1;

    private State gameState;

    private ArrayList<Player> players;

    @Autowired
    private Deck deck;

    @Autowired    
    private AIService aiService;
    
    private boolean waitingOnReal;
    
    // The current game state 
    public enum State {
        WAITING_FOR_ADMIN,
        WAITING_FOR_PLAYERS,
        PLAYING
    }
   
    @PostConstruct
    public void init() {
        this.players = new ArrayList<>();
        this.players.clear();
        this.gameState = State.WAITING_FOR_ADMIN;
        this.deck.reset();
        this.roundMaxPlayers = -1;
    }

	
    /**
     * Register AI players,  may have up to 3 AI 
     *
     * @param session the user's session.
     * @return true if the player was added successfully.
     */
    public boolean registerAIPlayer() {
        if (size(this.players) == DEFAULT_MAX_PLAYERS) {
            LOG.warn("Max players already reached!");
            return false;
        }else{
        	for(int i = this.players.size(); i < DEFAULT_MAX_PLAYERS; i++ ){
        		final int next = this.counter.incrementAndGet();
        	    String id =  String.format("AI-%d", next);
        		this.players.add(new AIPlayer(null, id));
        		LOG.info("Adding {} to the game.", id);
        	}
        	 return true;
        }
       
    }
    
    
    /**
     * Replace an existing player with an AI.
     *
     * @param session the old player's sesion.
     */
    public boolean registerReplacementAI(final WebSocketSession session) {
    	final int next = this.counter.incrementAndGet();
	    String id =  String.format("AI-%d", next);
        final AIPlayer aiPlayer = new AIPlayer(null,id);
        final Player old = this.getPlayerFor(session);
        old.getHand().getCards().forEach(card -> aiPlayer.getHand().addCard(card));
        aiPlayer.getHand().setHandStatus(old.getHand().getHandStatus());
        aiPlayer.setLastOption(old.getLastOption());

        //What do about the admin?
        if (((RealPlayer)old).isAdmin()) {
            LOG.info("Because the player was administrator, resetting to clean state.");
            return false;
        }


        this.players.remove(session.getId());
        this.players.add(aiPlayer);
        LOG.info("Replaced old player with new AI - copied cards.");
        return true;
    }



    /**
     * Register a new player in the game.
     *
     * @param session the user's session.
     * @return true if the player was added successfully.
     */
    public boolean registerPlayer(final WebSocketSession session) {
        RealPlayer player = new RealPlayer(session, session.getId());
    	// Set to admin if they are the first conneciton
        LOG.info("Player is trying to connect {}, {}", player.getuid(), player.getSession());
        if (this.players.isEmpty()) {
            LOG.info("Setting first player as admin:{}.", player.getuid());
            player.setAdmin(true);
    	}
        // Check that the player doesn't exist and add them
    	//if(!this.players.contains(player)){
           if(!this.players.contains(player)){
            	 LOG.info("Player is being added {}, {}", player.getuid(), player.getSession());
            	 return this.players.add(player);
           }
       
           return false;
    }

    /**
     * Remove a new player from the game.
     *
     * @param session the user's session.
     * @return true if the player was removed successfully.
     */
    public boolean deregisterPlayer(final WebSocketSession session) {
        if (this.isPlaying()) {
            if (this.isPlayerRegistered(session)) {
                LOG.info("Replacing {} with an AI prior to removing.", session.getId());
                return this.registerReplacementAI(session);
            } else {
                return true;
            }
        } else {
            return !this.isPlayerRegistered(session);
        }
    }

    /**
     * Remove all AI from the players list.
     *
     * @return true if we removed at least one.
     */
    public boolean deregisterAI() {
    	for(AIPlayer p: this.getConnectedAIPlayers()){
    		players.remove(p);
    	}
      
        return size(this.getConnectedAIPlayers()) != 0;
    }


    /**
     * Check to see if the session exists.
     *
     * @param session the session.
     * @return true if yes.
     */
    public boolean isPlayerRegistered(final WebSocketSession session) {
    	for(Player p: this.getConnectedRealPlayers()){
    		if(p.getSession() == session)
    			return true;
    			
    	}
    	return false;
      
    }
    public boolean isPlaying() {
        return this.gameState == State.PLAYING;
    }
    
    public boolean isWaitingForPlayers() {
        return this.gameState == State.WAITING_FOR_PLAYERS;
    }


    /**
     * Get the player sessions connected to this game including AI.
     *
     * @return the sessions.
     */
    public ArrayList<Player> getConnectedPlayers() {
        return this.players;
    }

	public void initiateHands() {
		LOG.info("Dealing initial Hand");
		this.gameState = State.PLAYING;
		// each player starts with 5 hidden cards
	    for(Player player: this.players) {
	            for(int i = 0; i < 5; i++){
	         	 final Card hiddenCard = this.deck.draw();
	            	hiddenCard.setHidden(true);
	            	player.getHand().addCard(hiddenCard);
	            }
	            player.getHand().sortRank();
	            System.out.printf("Dealt {} to {}.", player.getHand(), player.getuid());
	            LOG.info("Dealt {} to {}.", player.getHand(), player.getuid());
	        }
	}
	 /**
     * Perform the turn for the AI.
     *
     * @param ai the ai.
     */
    public void doAITurn(final AIPlayer player, boolean rigged){ 
    	LOG.info("Get AI Choice", player.getuid());
    	
    	List<Card> choice = this.aiService.getCardOption(player, this.getConnectedAIPlayers());
    	LOG.info("{} beeing processed!", player.getuid());
        
    	GameOption option ;
    	
    	if(choice.isEmpty() || choice == null){
    		option= GameOption.STAY;
    	       
    	}else{
    		option= GameOption.HIT;
    	}

    	player.setLastOption(option);
    	
    	if(!rigged){ // If the game is not rigged automate this 
    		LOG.info("{} will be using option {}!, discards {} ", player.getuid(), option, choice);
    	       
    		this.performOption(player, option, choice);
    	}
        
    }


    public void performOption(@NotNull final Player player, @NotNull final GameOption option,  final List<Card> cards) {
    	 player.setLastOption(option);      
    	LOG.info("Performing {} Option {}",player.getuid(),  option);
    	switch (option) {
            case HIT:
            	for(Card c: cards){
	            	int index = player.getHand().getCards().indexOf(c);
	                final Card drawn = this.deck.draw();
	                drawn.setHidden(false);
	                LOG.info("Drew {}.", drawn);
	                player.getHand().getCards().set(index,drawn);
            	}
                break;
            case STAY:
                LOG.info("{} is staying - do nothing.", player.getSession());
                break;
            default:
                throw new IllegalArgumentException("No valid argument passed to execute option.");
        }
    	 LOG.info("Setting option {}, {}", player, option);
        player.setLastOption(option);

    }
    // Let everyone see all cards
	public void revealCards(Player player) {
		LOG.info("revealing cards {}", player);
		player.getHand().getCards().forEach(card -> card.setHidden(false));
		
	}

	public void setWaitingOnReal(boolean b) {
		waitingOnReal = b;		
	}
	
	// open game Lobby, and wait for players to connect
	  public void openLobby(final int numberOfPlayers) {
	        if (numberOfPlayers < 1 || numberOfPlayers > 4) {
	            this.roundMaxPlayers = 4;
	        }
	        this.roundMaxPlayers = numberOfPlayers;
	        this.gameState = State.WAITING_FOR_PLAYERS;
	        LOG.info("Prepared new poker round for {} players.", numberOfPlayers);
	    }

	 // All Real players are connected, autofill remaining with AI
	public boolean readyToStart() {
		 final int numberRequired = this.roundMaxPlayers == -1 ? DEFAULT_MAX_PLAYERS : this.roundMaxPlayers;
	      LOG.info("Current number of players is {}. Required number is {}.", size(this.players), numberRequired);
	      return size(this.players) == numberRequired;
	}
	
    
     //Get the player sessions connected to this game including AI.
   public ArrayList<WebSocketSession> getConnectedPlayerSessions() {
    	ArrayList<WebSocketSession> temp = new ArrayList<>();
    	for(RealPlayer p: this.getConnectedRealPlayers()){
    		temp.add(p.getSession());
    		LOG.info("Sessions {}", p.getSession());
    	}
    	
    	return temp;
    }


         // Get the real players that are connected.
    public ArrayList<RealPlayer> getConnectedRealPlayers() {
    	ArrayList<RealPlayer> temp = new ArrayList<>();
    	for(Player p: this.players){
    		if(p.isReal()){
    			temp.add((RealPlayer)p);
    		}
    	}
        return temp;
    }

	public ArrayList<AIPlayer> getConnectedAIPlayers() {
	 	ArrayList<AIPlayer> temp = new ArrayList<>();
    	for(Player p: this.players){
    		if(p.isAI()){
    			temp.add((AIPlayer)p);
    		}
    	}
        return temp;
	}
    
    /**
     * Get the player for the given session.
     *
     * @param session the session.
     * @return the player.
     */
    public RealPlayer getPlayerFor(final WebSocketSession session) {
             for(RealPlayer p: this.getConnectedRealPlayers()){
            	 if(p.getSession().equals(session)){
            		 return p;
            	 }
             }
        return null;
    }
    

	public Player getAdmin() {
		for(RealPlayer p: this.getConnectedRealPlayers()){
			System.out.println(p.getuid()+" " +p.isAdmin());
			if(p.isAdmin()) return p;
		}
		return null;
	}


	public Player getNextPlayer() {
		 
		ArrayList<Player> ordering = new ArrayList<>();
		int size = 0;
		for(AIPlayer p: this.getConnectedAIPlayers()){
			if(p.getLastOption() == null){
				return p;
			}
		}
		// Add  real players who haven't played yet
		for(RealPlayer p: this.getConnectedRealPlayers()){
			if(p.getLastOption()== null){
				ordering.add(p);
				size++;
			}
			
		}
		
		// get the last player in the ordering
        return ordering.get(size-1);
   
	}
	
    /**
     * Build a list of messages to send to each player session, which contains the hand states of each card so far.
     *
     * @return the map of player keyed to their list of messages.
     */
    public Map<Player, List<TextMessage>> buildHandMessages() {
        final Map<Player, List<TextMessage>> messages = new HashMap<>();

        int otherPlayerIndex = 1;

        // We only need to do this for real players.
        for (final Player player : this.getConnectedRealPlayers()) {
        	player.getHand().sortRank();
            messages.putIfAbsent(player, new ArrayList<>());
            final List<TextMessage> playerMessages = messages.get(player);

            // Step 0, build the message that we're dealing the cards.
            playerMessages.add(message(MessageUtil.Message.DEALING_CARDS).build());

            // Step 1, build the messages to send the player their cards.
            player.getHand()
                    .getCards()
                    .forEach(card -> {
                        // Make it temporarily visible to the player (i.e we want to show it to the person).
                        if (card.isHidden()) {
                            card.setHidden(false);
                            playerMessages.add(message(MessageUtil.Message.ADD_PLAYER_CARD,
                                    card.toFormHTML(),
                                    player.getSession().getId()).build());
                            card.setHidden(true);
                        } else {
                            playerMessages.add(message(MessageUtil.Message.ADD_PLAYER_CARD,
                                    card.toFormHTML(),
                                    player.getSession().getId()).build());
                        }
                    });
     

            // Step 2, build the messages to send the player the other player's (AI's) cards.
            for (final Player playerOtherThanCurrent : this.getConnectedPlayers()) {
            	if(!player.equals(playerOtherThanCurrent)){
            		for (final Card card : playerOtherThanCurrent.getHand().getCards()) {
	                    playerMessages.add(message(MessageUtil.Message.ADD_OTHER_PLAYER_CARD,
	                            card.toFormHTML(),
	                            otherPlayerIndex,
	                            playerOtherThanCurrent.getuid())
	                            .build());
	                }
            		 otherPlayerIndex++;
	            }
            	  
            }

            otherPlayerIndex = 1;
        }
        return messages;
    }

    /**
     * Set the final statuses so we can send it out.
     */
    public List<Player> resolveRound() {
    	// Get highest value poker hand 
    	  PokerHand maxValue = PokerHand.HIGH_CARD;
    	 
  		
  		
  		
  		List<Player> order = this.players;
		int i, j, min_j;
		for ( i = 0 ; i < order.size(); i ++ ){
			order.get(i).getHand().setPokerValue(); // makes sure its set
	         min_j = i;
	         for ( j = i+1 ; j < order.size() ; j++ ) {
	        	 order.get(j).getHand().setPokerValue(); // makes sure its set
	        	 //if they are the same hand
	
	             if (order.get(j).getHand().getPokerValue().compareTo(order.get(min_j).getHand().getPokerValue()) == 0 ){
	            	 // find lowest valued card
	            	if(order.get(j).getHand().getHighCard().getValue() < order.get(min_j).getHand().getHighCard().getValue()){
		               
	               	 System.out.println(order.get(j).getHand().getPokerValue() + "  " + order.get(j).getHand().getHighCard() + " " +order.get(j).getHand().getHighCard().getValue());
		        	 System.out.println(order.get(min_j).getHand().getPokerValue() + "  " + order.get(j).getHand().getHighCard() + " " +order.get(min_j).getHand().getHighCard().getValue());
	            		min_j = j;   
	            	}
		        }// not the same hand 
	             else if (order.get(j).getHand().getPokerValue().compareTo(order.get(min_j).getHand().getPokerValue()) < 0 ){
	               min_j = j;   
	            }
	         }
	         //swap
	         Player temp = order.get(i);
	         order.set(i, order.get(min_j));
	         order.set(min_j, temp);

		}
	

		return order;

    }
    	  


	public boolean isResolved() {
        for (final Player player : this.getConnectedPlayers()) {
            if (player.getLastOption() == null){
            	LOG.info("{} still needs to play, {}", player.getuid(), player.getLastOption());	
            	return false;
            		
            }
        }
        LOG.info("Game is resolved");	
        
        return true;
	}
	
	   /**
     * Reset for another round.
     */
    public void resetRound() {
        for (final Player player : this.getConnectedPlayers()) {
            player.getHand().setHandStatus(null);
            player.getHand().clearHand();
            player.setLastOption(null);
        }
        this.setGameState(State.WAITING_FOR_PLAYERS);
        this.deck.reset();
        LOG.info("Round reset.");
    }


	private void setGameState(State state) {
		this.gameState = state;
		
	}


	public boolean AIDone() {
		for(AIPlayer ai :this.getConnectedAIPlayers()){
			if(ai.getLastOption() != null)
				return false;
		}
		return true;
	}


	public Player getPlayerFor(String id) {
		Player curr = null;
		for(Player p: this.players){
			System.out.print(p.getuid() +" "+id.trim());
			if(p.getuid().equals(id.trim())){
				return p;
			}
		}
		return curr;
	}


	public void resetPlayers() {
		for (final Player player : this.getConnectedPlayers()) {
            player.getHand().setHandStatus(null);
            player.setLastOption(null);
        }
        this.setGameState(State.WAITING_FOR_PLAYERS);
	}


	public AIPlayer getNextAI() {
		for(AIPlayer p: this.getConnectedAIPlayers()){
			if (p.getLastOption() == null)
				return p;
		}
		return null;
	}


}

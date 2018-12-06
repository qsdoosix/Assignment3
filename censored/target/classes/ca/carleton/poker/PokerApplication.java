package ca.carleton.poker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import ca.carleton.poker.game.PokerSocketHandler;

/**
 * Main class - launch the application and register endpoint handlers.
 *
 * 
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@EnableAutoConfiguration
@EnableWebSocket
@ComponentScan(basePackages = "ca.carleton.poker")
public class PokerApplication extends SpringBootServletInitializer implements WebSocketConfigurer {

    @Autowired
    private PokerSocketHandler PokerSocketHandler;

    public static void main(final String[] args) {
        SpringApplication.run(PokerApplication.class, args);
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this.PokerSocketHandler, "/game")
                .withSockJS();
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(PokerApplication.class);
    }

}

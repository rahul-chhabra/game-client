package com.mygame.client;

import java.util.Scanner;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.mygame.client.domain.GameState;
import com.mygame.client.domain.GameStatus;
import com.mygame.client.domain.Player;
import com.mygame.client.domain.PlayerState;

/**
* {@link GameClientRunner} is the main client class, and holds all the business logic to interact with the server and player.
* 
* @author Rahul
*
*/
@Component
@ConditionalOnNotWebApplication
@PropertySource("classpath:gameclient.properties")
public class GameClientRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameClientRunner.class);

	private int mainPlayerId = 0;
	private int otherPlayerId = 0;
	private Player mainPlayer;
	private Player otherPlayer;

	@Autowired
	RestTemplate restTemplate;

	private final String GAME_SERVER_URL;

	private static final String GET_PLAYER_ID_MSG = "\nPlease enter a valid player id ( 1 or 2).";
	private static final String GET_PLAYER_NAME_MSG = "\nPlease enter player name for Player %s.";
	private static final String WELCOME_MSG = "\n\n\tWelcome to the Game. \n Press Ctrl + C to exit anytime.";

	private static final String WAITING_MSG = "\nWAITING FOR OTHER PLAYER TO CONNECT and START Game";
	private static final String MOVE_MSG_MAIN_PLAYER = "\nIt's your turn %s, please enter column (1-9):";
	private static final String MOVE_MSG_OTHER_PLAYER = "\nWAITING FOR OTHER PLAYER TO MOVE";
	private static final String GAME_ALREADY_RUNNING_MSG = "\nOne Game is already in progress. Please try after some time.";

	private Scanner scanner;

	public GameClientRunner(@Value("${client.game_server_url}") String gameServerUrl) {
		this.GAME_SERVER_URL = gameServerUrl;
	}

	@PreDestroy
	public void destroy() {

		if (mainPlayerId == 1 || mainPlayerId == 2) {
			disconnectPlayer(mainPlayerId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(String.format("Player %d has been disconnected", mainPlayerId));
		}
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			startGameClient();
		} catch (Exception e) {
			LOGGER.error(String.format("%n%nShutting down the game Client. Please try later. %n [Error - %s] %n %n.",
					e.getMessage()));
		} finally {
			if (scanner != null)
				scanner.close();
		}
	}

	private void startGameClient() {

		// Game already running. Exit.
		if (isGameInProgres()) {
			System.out.println(GAME_ALREADY_RUNNING_MSG);
			return;
		} else
			System.out.println(WELCOME_MSG);

		scanner = new Scanner(System.in);

		// get Main Player
		getMainPlayer(scanner);

		// Connect main player
		connectPlayer(mainPlayerId);

		// Find other player
		findOtherPlayerId();
		otherPlayer = getPlayer(otherPlayerId);

		// Wait for second player to connect and start game
		waitForOtherPlayer();

		// startGame
		if (!isGameInProgres()) {
			startGame();
		}

		// play the game
		playGame(scanner);

		// DisplayTheWinner
		displayGameEndStatus();
	}

	private Player getPlayer(int playerId) {
		return restTemplate.getForObject(GAME_SERVER_URL + "/player/" + playerId, Player.class);
	}

	private void connectPlayer(int playerId) {
		restTemplate.put(GAME_SERVER_URL + "/player/" + mainPlayerId, PlayerState.CONNECT);
	}

	private void disconnectPlayer(int playerId) {
		restTemplate.put(GAME_SERVER_URL + "/player/" + mainPlayerId, PlayerState.DISCONNECT);
	}

	private GameStatus getGameStatus() {
		return restTemplate.getForObject(GAME_SERVER_URL + "/game/status", GameStatus.class);
	}

	private GameStatus startGame() {
		ResponseEntity<GameStatus> gameStatusEntity = restTemplate.postForEntity(GAME_SERVER_URL + "/game/start", null,
				GameStatus.class);
		return gameStatusEntity.getBody();
	}

	private GameStatus playerMove(Integer column) {
		ResponseEntity<GameStatus> gameStatusEntity = restTemplate
				.postForEntity(GAME_SERVER_URL + "/game/player/" + mainPlayerId + "/move", column, GameStatus.class);
		return gameStatusEntity.getBody();
	}

	private void getMainPlayer(Scanner scanner) {		
		
		System.out.println(GET_PLAYER_ID_MSG);	
		boolean isValidPlayerId = false;
		while(!isValidPlayerId) {
			try {
				mainPlayerId = Integer.parseInt(scanner.nextLine().trim());
				
				if(mainPlayerId!=1 && mainPlayerId !=2)
					throw new RuntimeException("Invalid player id. ");
				
				isValidPlayerId = true;
			}catch(Exception e){
				System.out.println(GET_PLAYER_ID_MSG);				
			}		
		}		
		
		mainPlayer = getPlayer(mainPlayerId);

		getMainPlayerName(scanner);
	}

	private void getMainPlayerName(Scanner scanner) {
		// Enter player name
		System.out.println(String.format(GET_PLAYER_NAME_MSG, mainPlayerId));
		
		String mainPlayerName="";
		
		boolean isValidPlayerName = false;
		
		while(!isValidPlayerName) {
			try {
				mainPlayerName = scanner.nextLine().trim();
				
				if(mainPlayerName == null || mainPlayerName.isEmpty())
					throw new RuntimeException("Invalid player name. ");
				
				isValidPlayerName = true;
			}catch(Exception e){
				System.out.println(String.format(GET_PLAYER_NAME_MSG, mainPlayerId));				
			}		
		}
		mainPlayer.setPlayerName(mainPlayerName);
	}

	private void displayGameEndStatus() {

		GameStatus gameStatus = getGameStatus();

		System.out.println(gameStatus.getBoardView());

		if (gameStatus.getPlayerWinner() == mainPlayerId) {
			System.out.println(
					String.format("Congratulations, you are the winner. Well Done %s !!!", mainPlayer.getPlayerName()));
		} else if (gameStatus.getPlayerWinner() == otherPlayerId) {
			System.out.println("Other Player is the winner.");
			System.out.println("Better Luck Next Time !!");
		} 
		else {
			System.out.println(gameStatus.getMessageForClient());
			System.out.println("Better Luck Next Time !!");
		}
	}

	private void playGame(Scanner scanner) {

		GameStatus gameStatus = getGameStatus();

		boolean showMessage = true;

		while (gameStatus.getGameState() == GameState.IN_PROGRESS) {

			// Check game status
			int whoseMove = gameStatus.getPlayerWhoseNextMove();

			if (whoseMove == mainPlayerId) {
				// Get board view after other player move
				System.out.println(gameStatus.getBoardView());

				try {
					System.out.println(String.format(MOVE_MSG_MAIN_PLAYER, mainPlayer.getPlayerName()));
					Integer column = Integer.parseInt(scanner.nextLine().trim());
					playerMove(column);
				} catch (RestClientException rce) {
					System.out.println(rce.getMessage());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				// Get board view after other self move
				gameStatus = getGameStatus();
				System.out.println(gameStatus.getBoardView());

				if (gameStatus.getGameState() == GameState.IN_PROGRESS)
					showMessage = true;
			} else {

				if (showMessage)
					System.out.println(MOVE_MSG_OTHER_PLAYER);

				try {
					// Check after 1 second
					Thread.sleep(1000);
					showMessage = false;

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			gameStatus = getGameStatus();
		}

	}

	boolean isGameInProgres() {
		return getGameStatus().getGameState() == GameState.IN_PROGRESS;
	}

	private void waitForOtherPlayer() {
		// Wait for second player to connect and start game
		if (otherPlayer.getPlayerState() == PlayerState.DISCONNECT)
			System.out.println(WAITING_MSG);

		while (otherPlayer.getPlayerState() == PlayerState.DISCONNECT) {

			try {
				// Check after 5 seconds
				Thread.sleep(5000);
				otherPlayer = getPlayer(otherPlayerId);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void findOtherPlayerId() {
		otherPlayerId = mainPlayerId == 1 ? 2 : 1;
	}
}

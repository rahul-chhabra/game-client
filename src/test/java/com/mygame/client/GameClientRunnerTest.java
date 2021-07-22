package com.mygame.client;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mygame.client.domain.GameState;
import com.mygame.client.domain.GameStatus;
import com.mygame.client.domain.Player;
import com.mygame.client.domain.PlayerState;

/**
 * @author Rahul
 *
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class GameClientRunnerTest {
	
	private final String MOCK_SERVER_URL ="http://localhost:8080";
			
	@InjectMocks
	private GameClientRunner gameClientRunner = new GameClientRunner(MOCK_SERVER_URL);
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@Mock
	private GameStatus mockGameStatus;
	
	@Mock
	private ResponseEntity<GameStatus> mockResponseEntity;
	
	@Mock
	private Player mockPlayer1;
	
	@Mock
	private Player mockPlayer2;
	
	@Test
	void whenGameAlreadyRunningThenExit() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockGameStatus.getGameState()).thenReturn(GameState.IN_PROGRESS);
		
		gameClientRunner.run();
		
		verify(mockGameStatus,times(1)).getGameState();
	}
	
	@Test
	void whenGameNotStartedAndServerConnectionBrokeThenExit() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockGameStatus.getGameState()).thenReturn(GameState.NOT_STARTED);
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/player/1", Player.class)).thenThrow(new RuntimeException("Error occurred"));
		
		String input = "1";
		input += "\n";
		input +="Rahul";
	    InputStream in = new ByteArrayInputStream(input.getBytes());
	    System.setIn(in);
		
		gameClientRunner.run();
		
		verify(mockGameStatus,times(1)).getGameState();
		verify(mockRestTemplate,times(1)).getForObject(MOCK_SERVER_URL+"/player/1", Player.class);
	}
	
	@Test 
	void whenGameAbortedByPlayerThenExit() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockGameStatus.getGameState()).thenReturn(GameState.NOT_STARTED);
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/player/1", Player.class)).thenReturn(mockPlayer1);
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/player/2", Player.class)).thenReturn(mockPlayer2);
		when(mockPlayer2.getPlayerState()).thenReturn(PlayerState.CONNECT);
		when(mockRestTemplate.postForEntity(MOCK_SERVER_URL+"/game/start", null, GameStatus.class)).thenReturn(mockResponseEntity);
		when(mockGameStatus.getGameState()).thenReturn(GameState.ABORTED);
		
		
		String input = "1";
		input += "\n";
		input +="Rahul";
	    InputStream in = new ByteArrayInputStream(input.getBytes());
	    System.setIn(in);
		
		gameClientRunner.run();
		
		verify(mockRestTemplate,atLeast(3)).getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class);
	}

}

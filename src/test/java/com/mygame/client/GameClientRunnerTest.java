package com.mygame.client;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.mygame.client.domain.GameState;
import com.mygame.client.domain.GameStatus;
import com.mygame.client.domain.Player;

/**
 * @author Rahul
 *
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@RunWith(MockitoJUnitRunner.class)
public class GameClientRunnerTest {
	
	private final String MOCK_SERVER_URL ="http://localhost:8080";
			
	@InjectMocks
	private GameClientRunner gameClientRunner = new GameClientRunner(MOCK_SERVER_URL);
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@Mock
	private GameStatus mockGameStatus;
	
	@Mock
	private Player mockPlayer1;
	
	@Mock
	private Player mockPlayer2;
	
//	@BeforeEach
//    public void setUp()  {
//		gameClientRunner = new GameClientRunner("http://localhost:8080");
//    }
	
	@Test
	public void test_1() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockGameStatus.getGameState()).thenReturn(GameState.IN_PROGRESS);
		
		gameClientRunner.run();
	}
	
//	@Test
	public void test_2() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockGameStatus.getGameState()).thenReturn(GameState.NOT_STARTED);
		
		gameClientRunner.run();
	}
	
//	@Test - working. Need to add full scenario
	public void test_3() throws Exception {
		
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/game/status", GameStatus.class)).thenReturn(mockGameStatus);
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/player/1", Player.class)).thenReturn(mockPlayer1);
		when(mockRestTemplate.getForObject(MOCK_SERVER_URL+"/player/2", Player.class)).thenReturn(mockPlayer2);
		when(mockGameStatus.getGameState()).thenReturn(GameState.NOT_STARTED);
		
		String input = "1";
		input += "\n";
		input +="rahul";
	    InputStream in = new ByteArrayInputStream(input.getBytes());
	    System.setIn(in);
		
		gameClientRunner.run();
	}

}

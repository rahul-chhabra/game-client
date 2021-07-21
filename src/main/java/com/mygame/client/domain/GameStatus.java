package com.mygame.client.domain;

/**
 * {@link GameStatus} represents the full status of the game.
 *
 * @author Rahul
 *
 */
public class GameStatus {

	private GameState gameState;
	private String boardView;
	private Integer playerWhoseNextMove;
	private Integer playerWinner;
	private String messageForClient;

	public GameState getGameState() {
		return gameState;
	}
	
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public String getBoardView() {
		return boardView;
	}
	
	public void setBoardView(String boardView) {
		this.boardView = boardView;
	}
	
	public Integer getPlayerWhoseNextMove() {
		return playerWhoseNextMove;
	}
	
	public void setPlayerWhoseNextMove(Integer playerWhoseNextMove) {
		this.playerWhoseNextMove = playerWhoseNextMove;
	}
	
	public Integer getPlayerWinner() {
		return playerWinner;
	}
	
	public void setPlayerWinner(Integer playerWinner) {
		this.playerWinner = playerWinner;
	}

	public String getMessageForClient() {
		return messageForClient;
	}

	public void setMessageForClient(String messageForClient) {
		this.messageForClient = messageForClient;
	}	
}

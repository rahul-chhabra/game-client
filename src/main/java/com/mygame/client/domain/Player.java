package com.mygame.client.domain;

/**
* {@link Player} is the main actor for game.
*
* @author Rahul
*
*/
public class Player {
	
	private Integer playerId;
	private PlayerState playerState;
	private String playerName;
	
	public Integer getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
	public PlayerState getPlayerState() {
		return playerState;
	}
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public String toString() {
		return "Player [playerId=" + playerId + ", playerState=" + playerState + ", playerName=" + playerName + "]";
	}
}

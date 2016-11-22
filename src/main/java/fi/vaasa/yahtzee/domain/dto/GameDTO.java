package fi.vaasa.yahtzee.domain.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fi.vaasa.yahtzee.domain.Category;
import fi.vaasa.yahtzee.domain.CategoryScore;
import fi.vaasa.yahtzee.domain.GameResult;
import fi.vaasa.yahtzee.domain.GameStatus;
import fi.vaasa.yahtzee.domain.Player;

public class GameDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String uuid;
	private GameStatus gameStatus;	
	private Player createdBy;
	
	private Player playerTurn;
	private Set<Player> players = new HashSet<>();	
	
	private int[] currentDices = new int[] {0, 0, 0, 0, 0};
	
	private GameResult gameResult;
	
	private Map<Player, Set<CategoryScore>> playerScores = new HashMap<>();	
	private Map<Player, Set<Category>> selectedCategories = new HashMap<>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public Player getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Player createdBy) {
		this.createdBy = createdBy;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Set<Player> players) {
		this.players = players;
	}

	public Player getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(Player playerTurn) {
		this.playerTurn = playerTurn;
	}

	public int[] getCurrentDices() {
		return currentDices;
	}

	public void setCurrentDices(int[] currentDices) {
		this.currentDices = currentDices;
	}

	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	public Map<Player, Set<CategoryScore>> getPlayerScores() {
		return playerScores;
	}

	public void setPlayerScores(Map<Player, Set<CategoryScore>> playerScores) {
		this.playerScores = playerScores;
	}

	public Map<Player, Set<Category>> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(Map<Player, Set<Category>> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}
	
}

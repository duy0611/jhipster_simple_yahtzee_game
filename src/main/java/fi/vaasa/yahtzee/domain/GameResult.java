package fi.vaasa.yahtzee.domain;

import java.util.HashMap;
import java.util.Map;

public class GameResult {

	private Player winner;
	private Map<Player, Integer> totalScores = new HashMap<>();
	
	public Player getWinner() {
		return winner;
	}
	public void setWinner(Player winner) {
		this.winner = winner;
	}
	public Map<Player, Integer> getTotalScores() {
		return totalScores;
	}
	public void setTotalScores(Map<Player, Integer> totalScores) {
		this.totalScores = totalScores;
	}
	
}

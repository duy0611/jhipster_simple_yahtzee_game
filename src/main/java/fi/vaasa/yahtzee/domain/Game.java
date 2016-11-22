package fi.vaasa.yahtzee.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import fi.vaasa.yahtzee.domain.exception.DuplicateCategoryException;

public class Game {
	
	public static final int MAX_NUMBER_PLAYERS = 2; //max number of players can join each game
	public static final int MIN_NUMBER_PLAYERS = 1; //min number of players to start game

	private String uuid; //unique id of each game
	
	private GameStatus gameStatus = GameStatus.WAITING_FOR_PLAYER;
	
	private Player createdBy; //name of player who has created this game firstly.
							  //only this play can start the game when having enough player
	
	private Set<Player> players = new HashSet<>();
	
	private Player playerTurn; //define which player can move next
	
	private Map<Player, Set<CategoryScore>> playerScores = new HashMap<>(); //scores of each player for each category
	
	private Map<Player, Set<Category>> selectedCategories = new HashMap<>(); //store list of selected categories for each player
	
	private GameResult gameResult;
	
	private int[] currentDices = new int[] {0, 0, 0, 0, 0};
	
	//Constructors
	public Game() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public Game(Player createdBy) {
		this.uuid = UUID.randomUUID().toString();
		
		this.gameStatus = GameStatus.WAITING_FOR_PLAYER;
		
		this.createdBy = createdBy;	
		this.playerTurn = createdBy;
		
		initNewPlayer(createdBy);
	}
	
	public void initNewPlayer(Player player) {
		
		this.players.add(player);
		
		playerScores.put(player, 
				Arrays.asList(Category.values()).stream().map(cat -> new CategoryScore(cat, -1)).collect(Collectors.toSet()));
		
		selectedCategories.put(player, new HashSet<>());
	}
	
	public int getNumnberOfPlayers() {
		return this.players.size();
	}
	
	/**
	 * Calculate score for selected category.
	 * @param selectedCategory
	 * @throws DuplicateCategoryException
	 */
	public void calculateScore(Category selectedCategory) throws DuplicateCategoryException {
		
		Player currentPlayer = getPlayerTurn();
		int[] dices = getCurrentDices();
		
		//check first if category is already selected
		if(isCategoryAlreadySelected(currentPlayer, selectedCategory)) {
			throw new DuplicateCategoryException("Category already selected: " + selectedCategory);
		}
		
		ArrayList<Integer> aces = new ArrayList<Integer>();
		ArrayList<Integer> twos = new ArrayList<Integer>();
		ArrayList<Integer> threes = new ArrayList<Integer>();
		ArrayList<Integer> fours = new ArrayList<Integer>();
		ArrayList<Integer> fives = new ArrayList<Integer>();
		ArrayList<Integer> sixes = new ArrayList<Integer>();

		for (int d : dices) {
			if (d == 1) {
				aces.add(1);
			} else if (d == 2) {
				twos.add(1);
			} else if (d == 3) {
				threes.add(1);
			} else if (d == 4) {
				fours.add(1);
			} else if (d == 5) {
				fives.add(1);
			} else if (d == 6) {
				sixes.add(1);
			}
		}
		
		int score = 0;
		
		switch(selectedCategory) {
		case ACES:
			score = 1*aces.size();
			break;
		case TWOS:
			score = 2*twos.size();
			break;
		case THREES:
			score = 3*threes.size();
			break;
		case FOURS:
			score = 4*fours.size();
			break;
		case FIVES:
			score = 5*fives.size();
			break;
		case SIXES:
			score = 6*sixes.size();
			break;
		default:
			throw new RuntimeException("Not supported category: " + selectedCategory);
		}
		
		//update selected category and category score
		getSelectedCategories().get(currentPlayer).add(selectedCategory);
		updateCategoryScore(currentPlayer, selectedCategory, score);
	}
	
	private void updateCategoryScore(Player currentPlayer, Category selectedCategory, int score) {
		Set<CategoryScore> list = this.getPlayerScores().get(currentPlayer);
		for (CategoryScore cs : list) {
			if(cs.getKey().equals(selectedCategory)) {
				cs.setValue(score);
				break;
			}
		}
	}
	
	private boolean isCategoryAlreadySelected(Player currentPlayer, Category selectedCategory) {
		Set<CategoryScore> list = this.getPlayerScores().get(currentPlayer);
		for (CategoryScore cs : list) {
			if(cs.getKey().equals(selectedCategory) && cs.getValue() >= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if game is finished.
	 * @return
	 */
	public boolean checkFinished() {
		if (getGameStatus() == GameStatus.STARTED) {
			boolean isGameFinished = true;
			//game is finished when all players has selected all categories
			for (Player p : getPlayers()) {
				if (getSelectedCategories().get(p).size() < Category.values().length) {
					isGameFinished = false;
					break;
				}
			}

			if (isGameFinished) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Complete game: calculate total scores and check winner.
	 */
	public void complete() {
		Player winner = null;
		Map<Player, Integer> totalScores = new HashMap<>();
		
		for (Player p : getPlayers()) {
			totalScores.put(p, getPlayerScores().get(p).stream().mapToInt(cs -> cs.getValue()).sum());		
		}
		
		winner = totalScores.entrySet().stream().max(Map.Entry.comparingByValue(Integer::compareTo)).get().getKey();
		
		gameResult = new GameResult();
		gameResult.setWinner(winner);
		gameResult.setTotalScores(totalScores);
	}

	/**
	 * Set turn to next player in list. The list can contain more than 2 players.
	 */
	public void setNextPlayerTurn() {
		Player currentPlayer = getPlayerTurn();
		int currentMove = getSelectedCategories().get(currentPlayer).size();
		
		for (Player p : getPlayers()) {
			if(!p.equals(currentPlayer)) {
				int playerMove = getSelectedCategories().get(p).size();
				if(playerMove < currentMove) {
					setPlayerTurn(p);
					return;
				}
			}
		}
		
		throw new RuntimeException("Invalid game state. Cannot move to next player's turn");
	}

	/**
	 * Roll dices
	 */
	public void rollDices() {
		Random rand = new Random(System.currentTimeMillis());
		for (int i=0; i<currentDices.length; i++) {
			currentDices[i] = rand.nextInt(6) + 1;
		}
	}

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

	public Player getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Player createdBy) {
		this.createdBy = createdBy;
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
	
}

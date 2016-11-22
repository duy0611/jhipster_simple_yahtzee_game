package fi.vaasa.yahtzee.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import fi.vaasa.yahtzee.domain.Category;
import fi.vaasa.yahtzee.domain.Game;
import fi.vaasa.yahtzee.domain.GameStatus;
import fi.vaasa.yahtzee.domain.Player;
import fi.vaasa.yahtzee.domain.exception.DuplicateCategoryException;
import fi.vaasa.yahtzee.domain.exception.GameFullException;
import fi.vaasa.yahtzee.domain.exception.GameNotFoundException;
import fi.vaasa.yahtzee.domain.exception.GameNotStartedException;
import fi.vaasa.yahtzee.domain.exception.InvalidGameException;

/**
 * Implementation of In-memory game repository
 */
@Service
@Scope(scopeName = org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InMemoryGameRepository {
	
	 private static final ConcurrentHashMap<String, Game> gameStore = new ConcurrentHashMap<>();
	 
	 /**
	  * Create new game.
	  * @param createdBy
	  * @return
	  */
	 public Game createNew(Player createdBy) {
		 Game game = new Game(createdBy);
		 
		 gameStore.put(game.getUuid(), game);
		 
		 return game;
	 }
	 
	 /**
	  * Find game by uuid.
	  * @param uuid
	  * @return
	  * @throws GameNotFoundException
	  */
	 public Game findByUuid(String uuid) throws GameNotFoundException {
		 Game game = gameStore.get(uuid);
		 if(game == null) {
			 throw new GameNotFoundException("Cannot find game with this uuid: " + uuid);
		 }
		 return game;
	 }
	 
	 /**
	  * Join existing game if not full.
	  * @param uuid
	  * @param newPlayer
	  * @return
	  * @throws GameNotFoundException
	  * @throws GameFullException
	  */
	 public Game join(String uuid, Player newPlayer) throws GameNotFoundException, GameFullException {
		 Game game = this.findByUuid(uuid);
		 
		 if(game.getNumnberOfPlayers() == Game.MAX_NUMBER_PLAYERS) {
			 throw new GameFullException("Game is full. Cannot join game. uuid: " + uuid);
		 }
		 
		 game.initNewPlayer(newPlayer);
		 
		 if(game.getNumnberOfPlayers() > Game.MIN_NUMBER_PLAYERS) {
			 game.setGameStatus(GameStatus.READY);
		 }
		 
		 return game;
	 }
	 
	 /**
	  * Start game when there is enough players.
	  * @param uuid
	  * @return
	  * @throws GameNotFoundException
	  * @throws GameNotStartedException 
	  */
	 public Game start(String uuid, Player currentPlayer) throws GameNotFoundException, GameNotStartedException {
		 Game game = this.findByUuid(uuid);
		 
		 if(game.getGameStatus() == GameStatus.READY) {
			 
			 if(!currentPlayer.equals(game.getCreatedBy())) {
				 throw new GameNotStartedException("Player is not authorized to start game. uuid: " + uuid);
			 }
			 
			 game.setGameStatus(GameStatus.STARTED);
		 }
		 else {
			 throw new GameNotStartedException("Game is not in READY stage. Not enough players to start game. uuid: " + uuid);
		 }
		 
		 return game;
	 }
	 
	 /**
	  * Roll the dice and let player select category. This is applied for current player's turn.
	  * @param uuid
	  * @return
	  * @throws GameNotFoundException
	  * @throws GameNotStartedException 
	  * @throws InvalidGameException 
	  */
	 public Game moveNext(String uuid, Player currentPlayer) throws GameNotFoundException, GameNotStartedException, InvalidGameException {
		 Game game = this.findByUuid(uuid);
		 
		 if(game.getGameStatus() == GameStatus.STARTED) {
			 
			 if(!currentPlayer.equals(game.getPlayerTurn())) {
				 throw new InvalidGameException("Player is not authorized to make a move. uuid: " + uuid);
			 }
			 
			 //roll new dices
			 game.rollDices();
			 
			 game.setGameStatus(GameStatus.WAIT_FOR_CATEGORY_SELECTED);
		 }
		 else {
			 throw new GameNotStartedException("Game is not in STARTED stage. uuid: " + uuid);
		 }
		 
		 return game;
	 }
	 
	 /**
	  * Complete current player's turn and set the turn to next player in list.
	  * @param uuid
	  * @return
	  * @throws GameNotFoundException
	  * @throws DuplicateCategoryException 
	  * @throws GameNotStartedException 
	  * @throws InvalidGameException 
	  */
	public Game finishTurn(String uuid, Player currentPlayer, Category selectedCategory)
			throws GameNotFoundException, DuplicateCategoryException, GameNotStartedException, InvalidGameException {
		Game game = this.findByUuid(uuid);

		if (game.getGameStatus() == GameStatus.WAIT_FOR_CATEGORY_SELECTED) {
			
			if(!currentPlayer.equals(game.getPlayerTurn())) {
				 throw new InvalidGameException("Player is not authorized to make a move. uuid: " + uuid);
			 }
			
			game.calculateScore(selectedCategory);

			// if game finishes, calculate total score and check winner
			if (game.checkFinished()) {
				game.complete();
				game.setGameStatus(GameStatus.FINISHED);
			} else {
				// otherwise, move to next player's turn
				game.setNextPlayerTurn();
				game.setGameStatus(GameStatus.STARTED);
			}
		} else {
			throw new GameNotStartedException("Game is not in WAIT_FOR_CATEGORY_SELECTED stage. uuid: " + uuid);
		}

		return game;
	}

	/**
	 * Get all games in memory-db
	 * @return
	 */
	public List<Game> getAll() {
		return new ArrayList<>(gameStore.values());
	}
	
	/**
	 * Clear all games in memory
	 */
	public void clearAll() {
		gameStore.clear();
	}
	
	/**
	 * Delete specific game by uuid
	 * @param uuid
	 */
	public void delete(String uuid) {
		gameStore.remove(uuid);
	}
	 
}

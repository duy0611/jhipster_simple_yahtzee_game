package fi.vaasa.yahtzee.domain;

public enum GameStatus {
	WAITING_FOR_PLAYER, //waiting for new players to join. it has to reach max number of players before game can start
	READY, //game is ready to start
	STARTED, //game in process
	WAIT_FOR_CATEGORY_SELECTED, //game in process, waiting for player to select category
	FINISHED, //when all player has finished their moves
	ABORTED //when one of players left and game cannot continue
}

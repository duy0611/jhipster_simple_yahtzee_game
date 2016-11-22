package fi.vaasa.yahtzee.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;

import fi.vaasa.yahtzee.SimpleYahtzeeApp;
import fi.vaasa.yahtzee.domain.Category;
import fi.vaasa.yahtzee.domain.Game;
import fi.vaasa.yahtzee.domain.GameStatus;
import fi.vaasa.yahtzee.domain.Player;
import fi.vaasa.yahtzee.domain.mapper.GameMapper;
import fi.vaasa.yahtzee.repository.InMemoryGameRepository;

/**
 * Test class for the GameResource REST controller.
 *
 * @see GameResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleYahtzeeApp.class)
public class GameResourceIntTest {

	@Inject
    private InMemoryGameRepository _gameRepository;
	
	@Inject
	private GameMapper gameMapper;

    private MockMvc restGameMockMvc;
    
    @Before
    public void setup() {
    	GameResource gameResource = new GameResource();
        ReflectionTestUtils.setField(gameResource, "_gameRepository", _gameRepository);
        ReflectionTestUtils.setField(gameResource, "gameMapper", gameMapper);
        this.restGameMockMvc = MockMvcBuilders.standaloneSetup(gameResource).build();
    }
    
    @Test
    public void testGetGameByUUID() throws Exception {	
    	Game game = _gameRepository.createNew(new Player("AAA"));
    	
    	restGameMockMvc.perform(get("/api/games/" + game.getUuid())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(game.getUuid())));
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testGameNotFound() throws Exception {
    	_gameRepository.createNew(new Player("AAA"));
    	
    	restGameMockMvc.perform(get("/api/games/" + "random-uuid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testCreateNewGame() throws Exception {
    	String username = "AAA";
    	
    	restGameMockMvc.perform(post("/api/games")
    			.with(request -> {
                    request.setRemoteUser(username);
                    return request;
                })
    			.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isCreated())
    			.andExpect(jsonPath("$.createdBy.playerName", is(username)))
    			.andExpect(jsonPath("$.gameStatus", is(GameStatus.WAITING_FOR_PLAYER.toString())));
    	
    }
    
    @Test
    public void testGetAllGames() throws Exception {
    	_gameRepository.createNew(new Player("AAA"));
    	_gameRepository.createNew(new Player("BBB"));
    	
    	restGameMockMvc.perform(get("/api/games")
    			.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$", hasSize(2)))
    			.andExpect(jsonPath("$[0].createdBy.playerName", isOneOf("AAA", "BBB")));
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testJoinGame() throws Exception {
    	String username = "BBB";
    	Game game = _gameRepository.createNew(new Player("AAA"));
    	
    	restGameMockMvc.perform(post("/api/games/" + game.getUuid() + "/join")
    			.with(request -> {
                    request.setRemoteUser(username);
                    return request;
                })
    			.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.createdBy.playerName", is("AAA")))
				.andExpect(jsonPath("$.playerTurn.playerName", is("AAA")))
				.andExpect(jsonPath("$.players", hasSize(2)))
				.andExpect(jsonPath("$.gameStatus", is(GameStatus.READY.toString())));
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testCannotJoinFullGame() throws Exception {
    	String username = "CCC";
    	Game game = _gameRepository.createNew(new Player("AAA"));
    	
    	_gameRepository.join(game.getUuid(), new Player("BBB"));
    	
    	restGameMockMvc.perform(post("/api/games/" + game.getUuid() + "/join")
    			.with(request -> {
                    request.setRemoteUser(username);
                    return request;
                })
    			.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testFinishTurn() throws Exception {
    	Player createdBy = new Player("AAA");
    	Game game = _gameRepository.createNew(createdBy);
    	
    	_gameRepository.join(game.getUuid(), new Player("BBB"));
    	_gameRepository.start(game.getUuid(), createdBy);
    	_gameRepository.moveNext(game.getUuid(), createdBy);
    	
    	_gameRepository.findByUuid(game.getUuid()).setCurrentDices(new int[] {1,1,1,4,5});
    	
    	restGameMockMvc.perform(post("/api/games/" + game.getUuid() + "/finishTurn?selectedCategory=" + Category.ACES.toString())
	    	.with(request -> {
	            request.setRemoteUser(createdBy.getPlayerName());
	            return request;
	        })
	    	.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.selectedCategories['Player name: AAA']", contains(Category.ACES.toString())))
			.andExpect(jsonPath("$.playerScores['Player name: AAA'][?(@.ACES)]['ACES']", contains(3)));
    	
    	_gameRepository.clearAll();
    }
    
    @Test
    public void testGameComplete() throws Exception {
    	Player createdBy = new Player("AAA");
    	Player joinPlayer = new Player("BBB");
    	Game game = _gameRepository.createNew(createdBy);
    	
    	_gameRepository.join(game.getUuid(), joinPlayer);
    	_gameRepository.start(game.getUuid(), createdBy);
    	
    	Category[] categories = Category.values();
    	
    	for (int i=0; i<categories.length; i++) {
    		_gameRepository.moveNext(game.getUuid(), createdBy);
    		_gameRepository.finishTurn(game.getUuid(), createdBy, categories[i]);
    		_gameRepository.moveNext(game.getUuid(), joinPlayer);
    		_gameRepository.finishTurn(game.getUuid(), joinPlayer, categories[i]);
    	}
    	
    	Assert.isTrue(game.checkFinished());
    	Assert.isTrue(game.getGameStatus().equals(GameStatus.FINISHED));
    	Assert.isTrue(game.getGameResult().getWinner() != null);
    	Assert.isTrue(game.getGameResult().getTotalScores().size() > 0);
    	
    	_gameRepository.clearAll();
    }
}

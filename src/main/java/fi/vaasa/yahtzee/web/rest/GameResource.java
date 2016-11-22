package fi.vaasa.yahtzee.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fi.vaasa.yahtzee.domain.Category;
import fi.vaasa.yahtzee.domain.Game;
import fi.vaasa.yahtzee.domain.Player;
import fi.vaasa.yahtzee.domain.dto.GameDTO;
import fi.vaasa.yahtzee.domain.exception.DuplicateCategoryException;
import fi.vaasa.yahtzee.domain.exception.GameFullException;
import fi.vaasa.yahtzee.domain.exception.GameNotFoundException;
import fi.vaasa.yahtzee.domain.exception.GameNotStartedException;
import fi.vaasa.yahtzee.domain.exception.InvalidGameException;
import fi.vaasa.yahtzee.domain.mapper.GameMapper;
import fi.vaasa.yahtzee.repository.InMemoryGameRepository;

/**
 * Rest controller for Yahtzee game (simplified version)
 */
@RestController
@RequestMapping("/api")
public class GameResource {

	private final Logger LOG = LoggerFactory.getLogger(GameResource.class);
	
	@Autowired
	InMemoryGameRepository _gameRepository;
	
	@Autowired
	GameMapper gameMapper;
	
	@GetMapping(value = "/games")
	@Timed
	public ResponseEntity<List<GameDTO>> getAllGames() {
		return new ResponseEntity<>(gameMapper.gamesToGameDTOs(_gameRepository.getAll()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/games/{uuid}")
	@Timed
	public ResponseEntity<GameDTO> getGame(@PathVariable("uuid") String uuid) {
		try {
			return new ResponseEntity<>(gameMapper.gameToGameDTO(_gameRepository.findByUuid(uuid)), HttpStatus.OK);
		} catch (GameNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(value = "/games")
	@Timed
	public ResponseEntity<?> createNewGame(HttpServletRequest request) throws URISyntaxException {
		Player createdBy = new Player();
		createdBy.setPlayerName(request.getRemoteUser());
		
		Game newGame = _gameRepository.createNew(createdBy);
		return ResponseEntity
				.created(new URI("/api/games/" + newGame.getUuid()))
				.body(gameMapper.gameToGameDTO(newGame));
	}
	
	@PostMapping(value = "/games/{uuid}/join")
	@Timed
	public ResponseEntity<GameDTO> joinGame(@PathVariable("uuid") String uuid, HttpServletRequest request) {
		Player newPlayer = new Player();
		newPlayer.setPlayerName(request.getRemoteUser());
		
		try {
			Game game = _gameRepository.join(uuid, newPlayer);
			return new ResponseEntity<>(gameMapper.gameToGameDTO(game), HttpStatus.OK);
			
		} catch (GameNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (GameFullException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/games/{uuid}/start")
	@Timed
	public ResponseEntity<GameDTO> startGame(@PathVariable("uuid") String uuid, HttpServletRequest request) {
		Player currentPlayer = new Player();
		currentPlayer.setPlayerName(request.getRemoteUser());
		
		try {
			Game game = _gameRepository.start(uuid, currentPlayer);
			return new ResponseEntity<>(gameMapper.gameToGameDTO(game), HttpStatus.OK);
			
		} catch (GameNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (GameNotStartedException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/games/{uuid}/moveNext")
	@Timed
	public ResponseEntity<GameDTO> moveNext(@PathVariable("uuid") String uuid, HttpServletRequest request) {
		Player currentPlayer = new Player();
		currentPlayer.setPlayerName(request.getRemoteUser());
		
		try {
			Game game = _gameRepository.moveNext(uuid, currentPlayer);
			return new ResponseEntity<>(gameMapper.gameToGameDTO(game), HttpStatus.OK);
			
		} catch (GameNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (GameNotStartedException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (InvalidGameException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/games/{uuid}/finishTurn")
	@Timed
	public ResponseEntity<GameDTO> finishTurn(@PathVariable("uuid") String uuid, @RequestParam("selectedCategory") Category selectedCategory, HttpServletRequest request) {
		Player currentPlayer = new Player();
		currentPlayer.setPlayerName(request.getRemoteUser());
		
		try {
			Game game = _gameRepository.finishTurn(uuid, currentPlayer, selectedCategory);
			return new ResponseEntity<>(gameMapper.gameToGameDTO(game), HttpStatus.OK);
			
		} catch (GameNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (GameNotStartedException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (InvalidGameException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (DuplicateCategoryException e) {
			LOG.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}

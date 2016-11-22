package fi.vaasa.yahtzee.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fi.vaasa.yahtzee.domain.Game;
import fi.vaasa.yahtzee.domain.dto.GameDTO;

/**
 * Mapper for the entity Game and its DTO GameDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GameMapper {

	GameDTO gameToGameDTO(Game game);

    List<GameDTO> gamesToGameDTOs(List<Game> users);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "gameStatus", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "playerTurn", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "currentDices", ignore = true)
    @Mapping(target = "gameResult", ignore = true)
    @Mapping(target = "playerScores", ignore = true)
    @Mapping(target = "selectedCategories", ignore = true)
    Game gameDTOToGame(GameDTO gameDTO);

    List<Game> gameDTOsToGames(List<GameDTO> gameDTOs);
    
}
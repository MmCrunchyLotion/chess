package dataaccess;

import models.*;

import java.util.Collection;

public interface GameDAO {

    static Collection<GameData> getDBGames() throws DataAccessException {
//        Return all ChessGames found in DB
        throw new DataAccessException("ListGames called. No DB reached");
    }

    static int createGame(String gameName) throws DataAccessException {
//        Create new game in DB
//        return the gameID given to new game
        throw new DataAccessException("CreateGame called. No DB reached");
    }

    static GameData findGame(int gameID) throws DataAccessException {
//        Find a game within the DB
//        return the gameData
        throw new DataAccessException("findGame called. No DB reached");
    }

    static void setUser(String username, String Color, int gameID) throws DataAccessException {
//        Update game with given ID to store username as matching team color
        throw new DataAccessException("SetUser called. No DB reached");
    }

    static void clear() throws DataAccessException {
//        Clear all games
        throw new DataAccessException("GameClear called. No DB reached");
    }
}

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

}

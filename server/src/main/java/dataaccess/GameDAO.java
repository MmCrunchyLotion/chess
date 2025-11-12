package dataaccess;

import models.*;
import mockdatabase.*;
import java.util.Collection;

public class GameDAO {

    private Games games;

    public GameDAO() {
        this.games = new Games();
    }

    public Collection<GameData> getDBGames() {
//        Return all ChessGames found in DB
        return games.getGames();
    }

    public int createGame(String gameName) {
//        Create new game in DB
//        return the gameID given to new game
        return games.addGame(new GameData(gameName));
    }

    public GameData findGame(int gameID) {
//        Find a game within the DB
//        return the gameData
        return games.findGame(gameID);
    }

    public void setUser(String username, String color, int gameID) {
//        Update game with given ID to store username as matching team color
        GameData game = games.findGame(gameID);
        if (color.equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                game.setWhiteUsername(username);
            }
        } else {
            game.setBlackUsername(username);
        }
    }

    public void clear() {
//        Clear all games
        games.clearGames();
    }
}

//public interface GameDAO {
//
//    static Collection<GameData> getDBGames() throws DataAccessException {
////        Return all ChessGames found in DB
//        throw new DataAccessException("ListGames called. No DB reached");
//    }
//
//    static int createGame(String gameName) throws DataAccessException {
////        Create new game in DB
////        return the gameID given to new game
//        throw new DataAccessException("CreateGame called. No DB reached");
//    }
//
//    static GameData findGame(int gameID) throws DataAccessException {
////        Find a game within the DB
////        return the gameData
//        throw new DataAccessException("findGame called. No DB reached");
//    }
//
//    static void setUser(String username, String Color, int gameID) throws DataAccessException {
////        Update game with given ID to store username as matching team color
//        throw new DataAccessException("SetUser called. No DB reached");
//    }
//
//    static void clear() throws DataAccessException {
////        Clear all games
//        throw new DataAccessException("GameClear called. No DB reached");
//    }
//}

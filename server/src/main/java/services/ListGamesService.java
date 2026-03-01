package services;

import dataaccess.*;
import exception.ResponseException;
import models.AuthData;
import models.GameData;

import java.util.Collection;

import static exception.ResponseException.Code.*;

public class ListGamesService extends Service {

    private final GameDAO gameDAO;

    public ListGamesService(AuthData auth, AuthDAO authDAO, GameDAO gameDAO) throws ResponseException {
        this.gameDAO = gameDAO;
        checkAuth(auth, authDAO);
    }

    public Collection<GameData> list() throws ResponseException {
        try {
            return gameDAO.getAllGames();
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }
}
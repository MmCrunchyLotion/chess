package services;

import java.util.Collection;
import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class ListGamesService {

    private Collection<GameData> games;

    public ListGamesService(AuthData auth) throws ResponseException, DataAccessException {
        AuthDAO.getAuth(auth);
        if (auth == null) {
            throw new ResponseException(Unauthorized, "No user associated with token received");
        }
    }

    public Collection<GameData> list() throws DataAccessException {
        this.games = GameDAO.getDBGames();
        return this.games;
    }
}

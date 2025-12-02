package services;

import java.util.Collection;
import exception.ResponseException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class ListGamesService extends Service {

    private Collection<GameData> games;
    private GameDAO mockGameDAO;

    public ListGamesService(AuthData auth, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        this.mockGameDAO = mockGameDAO;
        checkAuth(auth, mockAuthDAO);
    }

    public Collection<GameData> list() {
        this.games = mockGameDAO.getDBGames();
        return this.games;
    }
}

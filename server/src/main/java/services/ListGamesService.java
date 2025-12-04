package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import java.util.Collection;
import models.*;

public class ListGamesService extends Service {

    private final GameDAO mockGameDAO;

    public ListGamesService(AuthData auth, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        this.mockGameDAO = mockGameDAO;
        checkAuth(auth, mockAuthDAO);
    }

    public Collection<GameData> list() {
        return mockGameDAO.getDBGames();
    }
}

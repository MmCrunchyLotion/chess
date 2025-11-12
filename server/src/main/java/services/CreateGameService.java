package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class CreateGameService extends Service{

    private GameData game;
    private GameDAO mockGameDAO;

    public CreateGameService(AuthData auth, GameData game, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        this.game = game;
        checkAuth(auth, mockAuthDAO);
        this.mockGameDAO = mockGameDAO;
    }

    public void addGame() throws ResponseException {
        if (this.game.getGameName() == null) {
            throw new ResponseException(ClientError, "No Game name received");
        }
        game.setGameID(mockGameDAO.createGame(this.game.getGameName()));
    }

}

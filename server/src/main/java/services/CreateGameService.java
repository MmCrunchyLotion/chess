package services;

import static exception.ResponseException.Code.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import models.*;

public class CreateGameService extends Service {

    private final GameData game;
    private final GameDAO mockGameDAO;

    public CreateGameService(AuthData auth, GameData game, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        this.game = game;
        this.mockGameDAO = mockGameDAO;
        checkAuth(auth, mockAuthDAO);
    }

    public GameData addGame() throws ResponseException {
        if (this.game.getGameName() == null) {
            throw new ResponseException(ClientError, "Error: No Game name received");
        }
        game.setGameID(mockGameDAO.createGame(this.game.getGameName()));
        return game;
    }
}

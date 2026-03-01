package services;

import static exception.ResponseException.Code.*;
import dataaccess.*;
import exception.ResponseException;
import models.AuthData;
import models.GameData;

public class CreateGameService extends Service {

    private final GameData game;
    private final GameDAO gameDAO;

    public CreateGameService(AuthData auth, GameData game, AuthDAO authDAO, GameDAO gameDAO) throws ResponseException {
        this.game = game;
        this.gameDAO = gameDAO;
        checkAuth(auth, authDAO);
    }

    public GameData addGame() throws ResponseException {
        if (this.game.getGameName() == null) {
            throw new ResponseException(ClientError, "Error: No game name received");
        }
        try {
            game.setGameID(gameDAO.createGame(this.game.getGameName()));
            return game;
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }
}
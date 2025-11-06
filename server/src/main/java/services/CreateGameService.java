package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class CreateGameService extends Service{

    private GameData game;

    public CreateGameService(AuthData auth, GameData game) throws ResponseException, DataAccessException {
        this.game = game;
        checkAuth(auth);
    }

    public void addGame() throws ResponseException, DataAccessException {
        if (this.game.getGameName() == null) {
            throw new ResponseException(ClientError, "No Game name received");
        }
        game.setGameID(GameDAO.createGame(this.game.getGameName()));
    }

}

package services;

import java.util.Collection;
import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class CreateGameService {

    private GameData game;

    public CreateGameService(AuthData auth, GameData game) throws ResponseException, DataAccessException {
        this.game = game;
        AuthDAO.getAuth(auth);
        if (auth == null) {
            throw new ResponseException(Unauthorized, "No user associated with token received");
        }
    }

    public void addGame() throws DataAccessException {
        game.setGameID(GameDAO.createGame(this.game.getGameName()));
    }

}

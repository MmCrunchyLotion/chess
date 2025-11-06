package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class JoinGameService extends Service {

    private GameData game;

    public JoinGameService(AuthData auth, GameData game) throws ResponseException, DataAccessException {
        checkAuth(auth);
        this.game = GameDAO.findGame(game.getGameID());
        if (game == null) {
            throw new ResponseException(ServerError, "Game not found");
        }
    }

    public void addPlayer(GameData game, String username, String playerColor) throws ResponseException, DataAccessException {
        if (playerColor.equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Team already taken");
            }
            GameDAO.setUser(username,"WHITE", game.getGameID());
        } else {
            if (game.getBlackUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Team already taken");
            }
            GameDAO.setUser(username,"BLACK", game.getGameID());

        }
    }

}

package services;

import static exception.ResponseException.Code.*;
import dataaccess.*;
import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.JoinBody;

public class JoinGameService extends Service {

    private final GameData game;
    private final JoinBody join;
    private final String username;

    public JoinGameService(AuthData auth, JoinBody join, AuthDAO authDAO, GameDAO gameDAO) throws ResponseException {
        checkAuth(auth, authDAO);
        try {
            this.game = gameDAO.getGame(join.getGameID());
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
        this.join = join;
        this.username = getUserByAuth(auth, authDAO);
        if (game == null) {
            throw new ResponseException(ClientError, "Error: Game not found");
        }
    }

    public void addPlayer(GameDAO gameDAO) throws ResponseException {
        String color = join.getPlayerColor();
        if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
            throw new ResponseException(ClientError, "Error: bad request");
        }
        try {
            if (color.equals("WHITE")) {
                if (game.getWhiteUsername() != null) {
                    throw new ResponseException(AlreadyTaken, "Error: Team already taken");
                }
                gameDAO.setUser(username, "WHITE", game.getGameID());
            } else {
                if (game.getBlackUsername() != null) {
                    throw new ResponseException(AlreadyTaken, "Error: Team already taken");
                }
                gameDAO.setUser(username, "BLACK", game.getGameID());
            }
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }
}
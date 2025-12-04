package services;

import static exception.ResponseException.Code.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import models.*;

public class JoinGameService extends Service {

    private final GameData game;
    private final JoinBody join;
    private final String username;

    public JoinGameService(AuthData auth, JoinBody join, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        checkAuth(auth, mockAuthDAO);
        this.game = mockGameDAO.findGame(join.getGameID());
        this.join = join;
        this.username = getUserByAuth(auth, mockAuthDAO);
        if (game == null) {
            throw new ResponseException(ClientError, "Error: Game not found");
        }
    }

    public void addPlayer(GameDAO mockGameDAO) throws ResponseException {
        String color = join.getPlayerColor();
        if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
            throw new ResponseException(ClientError, "Error: bad request");
        }
        if (join.getPlayerColor().equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Error: Team already taken");
            }
            mockGameDAO.setUser(username,"WHITE", game.getGameID());
        } else {
            if (game.getBlackUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Error: Team already taken");
            }
            mockGameDAO.setUser(username,"BLACK", game.getGameID());
        }
    }
}

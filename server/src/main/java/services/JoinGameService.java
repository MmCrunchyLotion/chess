package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import models.*;

public class JoinGameService extends Service {

    private GameData game;
    private JoinBody join;

    public JoinGameService(AuthData auth, JoinBody join, AuthDAO mockAuthDAO, GameDAO mockGameDAO) throws ResponseException {
        checkAuth(auth, mockAuthDAO);
        this.game = mockGameDAO.findGame(join.getGameID());
        this.join = join;
        if (game == null) {
            throw new ResponseException(ServerError, "Game not found");
        }
    }

    public void addPlayer(String username, GameDAO mockGameDAO) throws ResponseException {
        if (join.getPlayerColor().equals("WHITE")) {
            if (game.getWhiteUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Team already taken");
            }
            mockGameDAO.setUser(username,"WHITE", game.getGameID());
        } else {
            if (game.getBlackUsername() != null) {
                throw new ResponseException(AlreadyTaken, "Team already taken");
            }
            mockGameDAO.setUser(username,"BLACK", game.getGameID());
        }
    }

}

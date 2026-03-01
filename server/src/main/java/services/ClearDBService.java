package services;

import dataaccess.*;
import exception.ResponseException;
import static exception.ResponseException.Code.*;

public class ClearDBService {

    public ClearDBService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) throws ResponseException {
        try {
            authDAO.clear();
            gameDAO.clear();
            userDAO.clear();
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }
}
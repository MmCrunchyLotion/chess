package services;

import dataaccess.*;
import exception.ResponseException;
import models.AuthData;
import models.UserData;
import static exception.ResponseException.Code.*;

public class Service {

    public void checkAuth(AuthData auth, AuthDAO authDAO) throws ResponseException {
        if (auth == null || auth.getAuthToken() == null) {
            throw new ResponseException(Unauthorized, "Error: unauthorized");
        }
        try {
            AuthData authDB = authDAO.getAuthByToken(auth.getAuthToken());
            if (authDB == null) {
                throw new ResponseException(Unauthorized, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }

    public String getUserByAuth(AuthData auth, AuthDAO authDAO) throws ResponseException {
        try {
            AuthData authDB = authDAO.getAuthByToken(auth.getAuthToken());
            return authDB.getUsername();
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }

    public void checkNullFields(UserData user) throws ResponseException {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new ResponseException(ClientError, "Error: missing required fields");
        }
    }
}
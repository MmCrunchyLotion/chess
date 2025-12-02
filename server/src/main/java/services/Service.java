package services;

import dataaccess.*;
import exception.ResponseException;
import models.*;

import static exception.ResponseException.Code.*;

public class Service {

    public void checkAuth (AuthData auth, AuthDAO mockAuthDAO) throws ResponseException {
        AuthData authDB = mockAuthDAO.getAuthByToken(auth);
        if (auth == null|| auth.getAuthToken() == null) {
            throw new ResponseException(Unauthorized, "Error: unauthorized");
        }
        if (authDB == null) {
            throw new ResponseException(Unauthorized, "Error: unauthorized");
        }
    }

    public String getUserByAuth (AuthData auth, AuthDAO mockAuthDAO) {
        AuthData authDB = mockAuthDAO.getAuthByToken(auth);
        return authDB.getUsername();
    }

    public void checkNullFields (UserData user) throws ResponseException {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new ResponseException(ClientError, "Error: missing required fields");
        }
    }
}

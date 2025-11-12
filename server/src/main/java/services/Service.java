package services;

import dataaccess.AuthDAO;
import exception.ResponseException;
import models.AuthData;

import static exception.ResponseException.Code.Unauthorized;

public class Service {

    public void checkAuth (AuthData auth, AuthDAO mockAuthDAO) throws ResponseException {
        mockAuthDAO.getAuth(auth);
        if (auth == null) {
            throw new ResponseException(Unauthorized, "No user associated with token received");
        }
    }
}

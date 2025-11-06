package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import models.*;

public class Logout extends Service {

    private AuthData auth;

    public Logout(AuthData auth) throws ResponseException, DataAccessException {
        checkAuth(auth);
    }

    public void logout() throws DataAccessException {
        AuthDAO.removeAuth(auth);
    }
}

package services;

import dataaccess.*;
import exception.ResponseException;
import models.*;
import static exception.ResponseException.Code.*;

public class LogoutService extends Service {

    private final AuthData auth;
    private final AuthDAO authDAO;

    public LogoutService(AuthData auth, AuthDAO authDAO) throws ResponseException {
        this.authDAO = authDAO;
        this.auth = auth;
        checkAuth(auth, authDAO);
    }

    public void logout() throws ResponseException {
        try {
            authDAO.removeAuth(auth.getAuthToken());
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }
}
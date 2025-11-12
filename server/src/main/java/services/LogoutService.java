package services;

import exception.ResponseException;
import dataaccess.AuthDAO;
import models.*;

public class LogoutService extends Service {

    private AuthData auth;
    private AuthDAO mockAuthDAO;

    public LogoutService(AuthData auth, AuthDAO mockAuthDAO) throws ResponseException {
        this.mockAuthDAO = mockAuthDAO;
        checkAuth(auth, mockAuthDAO);
        this.auth = auth;
    }

    public void logout() {
        mockAuthDAO.removeAuth(auth);
    }
}

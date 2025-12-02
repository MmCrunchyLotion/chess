package services;

import exception.ResponseException;
import dataaccess.AuthDAO;
import models.*;

public class LogoutService extends Service {

    private AuthData auth;
    private AuthDAO mockAuthDAO;

    public LogoutService(AuthData auth, AuthDAO mockAuthDAO) throws ResponseException {
        this.mockAuthDAO = mockAuthDAO;
        this.auth = auth;
        checkAuth(auth, mockAuthDAO);
    }

    public void logout() {
        mockAuthDAO.removeAuth(auth);
    }
}

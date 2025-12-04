package services;

import dataaccess.AuthDAO;
import exception.ResponseException;
import models.*;

public class LogoutService extends Service {

    private final AuthData auth;
    private final AuthDAO mockAuthDAO;

    public LogoutService(AuthData auth, AuthDAO mockAuthDAO) throws ResponseException {
        this.mockAuthDAO = mockAuthDAO;
        this.auth = auth;
        checkAuth(auth, mockAuthDAO);
    }

    public void logout() {
        mockAuthDAO.removeAuth(auth);
    }
}

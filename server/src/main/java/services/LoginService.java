package services;

import static exception.ResponseException.Code.*;
import dataaccess.*;
import exception.ResponseException;
import models.*;

public class LoginService extends Service {

    private final UserData user;
    private AuthData auth;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserData user, UserDAO userDAO, AuthDAO authDAO) {
        this.user = user;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void login() throws ResponseException {
        checkNullFields(user);
        try {
            if (!userDAO.verifyPassword(user.getUsername(), user.getPassword())) {
                throw new ResponseException(Unauthorized, "Error: Incorrect username or password.");
            }
            auth = new AuthData(user.getUsername(), null);
            authDAO.addAuth(auth);
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }

    public AuthData getAuth() {
        return auth;
    }
}
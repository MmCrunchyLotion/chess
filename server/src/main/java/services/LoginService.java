package services;

import java.util.Objects;
import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class LoginService extends Service {

    private UserData user;
    private AuthData auth;
    private UserDAO mockUserDAO;
    private AuthDAO mockAuthDAO;

    public LoginService (UserData user, UserDAO mockUserDAO, AuthDAO mockAuthDAO) {
        this.user = user;
        this.mockUserDAO = mockUserDAO;
        this.mockAuthDAO = mockAuthDAO;
    }

    public AuthData login() throws ResponseException {
        checkNullFields(user);
        UserData userDB = mockUserDAO.getUser(user.getUsername());
        if (userDB == null || !Objects.equals(user.getPassword(), userDB.getPassword())) {
            throw new ResponseException(Unauthorized, "Error: Incorrect username or password.");
        } else {
            this.auth = new AuthData(user.getUsername(), null);
            mockAuthDAO.addAuth(auth);
            return auth;
        }
    }

    public AuthData getAuth() {
        return auth;
    }
}

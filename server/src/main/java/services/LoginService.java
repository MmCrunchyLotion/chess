package services;

import static exception.ResponseException.Code.*;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import exception.ResponseException;
import java.util.Objects;
import models.*;

public class LoginService extends Service {

    private final UserData user;
    private AuthData auth;
    private final UserDAO mockUserDAO;
    private final AuthDAO mockAuthDAO;

    public LoginService (UserData user, UserDAO mockUserDAO, AuthDAO mockAuthDAO) {
        this.user = user;
        this.mockUserDAO = mockUserDAO;
        this.mockAuthDAO = mockAuthDAO;
    }

    public void login() throws ResponseException {
        checkNullFields(user);
        UserData userDB = mockUserDAO.getUser(user.getUsername());
        if (userDB == null || !Objects.equals(user.getPassword(), userDB.getPassword())) {
            throw new ResponseException(Unauthorized, "Error: Incorrect username or password.");
        }
        auth = new AuthData(user.getUsername(), null);
        mockAuthDAO.addAuth(auth);
    }

    public AuthData getAuth() {
        return auth;
    }
}

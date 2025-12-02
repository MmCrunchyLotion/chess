package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class RegisterUserService extends Service {

    private AuthData auth;
    private UserData user;
    private UserDAO mockUserDAO;
    private AuthDAO mockAuthDAO;

    public RegisterUserService(UserData user, UserDAO mockUserDAO, AuthDAO mockAuthDAO) {
        this.user = user;
        this.mockUserDAO = mockUserDAO;
        this.mockAuthDAO = mockAuthDAO;
    }

    public void register() throws ResponseException {
        checkNullFields(user);
        UserData DBUser = mockUserDAO.getUser(user.getUsername());
        String DBUsername = null;
        if (DBUser != null) {
            DBUsername = DBUser.getUsername();
        }
        String username = user.getUsername();
        if (DBUser != null && DBUsername.equals(username)) {
            throw new ResponseException(AlreadyTaken, "Error: Username already taken.");
        } else {
            mockUserDAO.createUser(user);
            this.auth = new AuthData(user.getUsername(), null);
            mockAuthDAO.addAuth(auth);
        }
    }

    public AuthData getAuth() {
        return auth;
    }
}

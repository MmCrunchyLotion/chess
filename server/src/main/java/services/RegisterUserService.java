package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class RegisterUserService {

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
        if (mockUserDAO.getUser(user.getUsername()).getUsername().equals(user.getUsername())) {
            throw new ResponseException(AlreadyTaken, "Error: Username already taken.");
        } else {
            mockUserDAO.createUser(user);
            this.auth = new AuthData(user.getUsername());
            mockAuthDAO.addAuth(auth);
        }
    }
}

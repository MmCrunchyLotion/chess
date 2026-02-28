package services;

import static exception.ResponseException.Code.*;
import dataaccess.*;
import exception.ResponseException;
import models.*;

public class RegisterUserService extends Service {

    private AuthData auth;
    private final UserData user;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterUserService(UserData user, UserDAO userDAO, AuthDAO authDAO) {
        this.user = user;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void register() throws ResponseException {
        checkNullFields(user);
        try {
            UserData DBUser = userDAO.getUser(user.getUsername());
            if (DBUser != null && DBUser.getUsername().equals(user.getUsername())) {
                throw new ResponseException(AlreadyTaken, "Error: Username already taken.");
            }
            userDAO.createUser(user);
            this.auth = new AuthData(user.getUsername(), null);
            authDAO.addAuth(auth);
        } catch (DataAccessException e) {
            throw new ResponseException(ServerError, e.getMessage());
        }
    }

    public AuthData getAuth() {
        return auth;
    }
}
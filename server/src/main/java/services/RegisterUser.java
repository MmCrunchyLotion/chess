package services;

import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class RegisterUser {

    private AuthData auth;

    public RegisterUser(UserData user) throws ResponseException, DataAccessException {
        if (UserDAO.getUser(user.getUsername()) != null) {
            throw new ResponseException(AlreadyTaken, "Error: Username already taken.");
        } else {
            try {
                UserDAO.createUser(user);
                this.auth = new AuthData(user.getUsername());
                AuthDAO.addAuth(auth);
            } catch (DataAccessException ex) {
                throw new DataAccessException("RegisterUser called, attempted to create user and authToken. No DB reached");
            }
        }
    }
}

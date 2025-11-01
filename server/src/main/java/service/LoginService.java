package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import models.UserData;

import static exception.ResponseException.Code.*;

public class LoginService {

    private UserData user;

    private void login(String username) throws ResponseException {
        try {
            this.user = UserDAO.getUser(username);
        } catch (DataAccessException ex) {
            throw new ResponseException(Unauthorized, "Error: Incorrect username or password.");
        }
    }
}

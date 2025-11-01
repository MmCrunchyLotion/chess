package services;

import java.util.Objects;
import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class Login {

    private AuthData auth;

    public Login(UserData user) throws ResponseException, DataAccessException {
        UserData userDB = UserDAO.getUser(user.getUsername());
        if (userDB == null || !Objects.equals(user.getPassword(), userDB.getPassword())) {
            throw new ResponseException(Unauthorized, "Error: Incorrect username or password.");
        } else {
            try {
                this.auth = new AuthData(user.getUsername());
                AuthDAO.addAuth(auth);
            } catch (DataAccessException ex) {
                throw new DataAccessException("Login called, attempted to create authToken. No DB reached");
            }
        }
    }
}

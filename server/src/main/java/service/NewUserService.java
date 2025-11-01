package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import exception.ResponseException;
import models.*;

import static exception.ResponseException.Code.*;

public class NewUserService {

    private AuthData auth;

    public NewUserService(UserData user) throws ResponseException, DataAccessException {
        if (UserDAO.getUser(user.getUsername()) != null) {
            throw new ResponseException(AlreadyTaken, "Error: Username already taken.");
        } else {
            try {
                UserDAO.createUser(user);
                this.auth = new AuthData(user.getUsername());
                AuthDAO.addAuth(auth, user);
            } catch (DataAccessException ex) {
                throw new DataAccessException("NewUserService called attempted to create user and authToken. No DB reached");
            }
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

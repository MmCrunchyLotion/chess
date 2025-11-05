package services;

import java.util.Objects;
import exception.ResponseException;
import static exception.ResponseException.Code.*;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import models.*;

public class Logout {

    private AuthData auth;

    public Logout(AuthData auth) throws ResponseException, DataAccessException {
        this.auth = auth;
        AuthDAO.getAuth(auth);
        if (auth != null) {
            AuthDAO.removeAuth(auth);
        } else {
            throw new ResponseException(Unauthorized, "No user associated with token received");
        }
    }
}

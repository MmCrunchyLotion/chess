package dataaccess;

import models.*;
import mockdatabase.*;

public class AuthDAO {

    private AuthTokens tokens;

    public AuthDAO() {
        this.tokens = new AuthTokens();
    }

    public AuthData addAuth(AuthData auth) {
//        Add auth to DB
//        Return auth
//        AuthData token = new AuthData(auth.getUsername());
        tokens.addToken(auth);
        return auth;
    }

    public AuthData getAuthByToken(AuthData auth) {
//        Find an auth from the DB
//        Return auth
        if (auth == null || auth.getAuthToken() == null) {
            return null;
        }
        for (AuthData authDB : tokens.getTokens()) {
            if (authDB.getAuthToken().equals(auth.getAuthToken())) {
                return authDB;
            }
        }
        return null;
    }

    public AuthData getAuthByUser(String username) {
        for (AuthData authDB : tokens.getTokens()) {
            if (authDB.getUsername().equals(username)) {
                return authDB;
            }
        }
        return null;
    }

    public void removeAuth(AuthData auth) {
//        remove auth from DB
        if (auth == null || auth.getAuthToken() == null) {
            return;
        }
        AuthData toRemove = null;
        for (AuthData authDB : tokens.getTokens()) {
            if (authDB.getAuthToken().equals(auth.getAuthToken())) {
                toRemove = authDB;
            }
        }
        if (toRemove != null) {
            tokens.removeToken(toRemove);
        }
    }

    public void clear() {
//        Clear all auth tokens
        tokens.clearTokens();
    }
}
//
//public interface AuthDAO {
//
//    static AuthData addAuth(AuthData auth) throws DataAccessException {
////        Add auth to DB
////        Return auth
//        throw new DataAccessException("AddAuth called. No DB reached");
//    }
//
//    static AuthData getAuth(AuthData auth) throws DataAccessException {
////        Find an auth from the DB
////        Return auth
//        throw new DataAccessException("GetAuth called. No DB reached");
//    }
//
//    static void removeAuth(AuthData auth) throws DataAccessException {
////        remove auth from DB
//        throw new DataAccessException("RemoveAuth called. No DB reached");
//    }
//
//    static void clear() throws DataAccessException {
////        Clear all auth tokens
//        throw new DataAccessException("AuthClear called. No DB reached");
//    }
//}

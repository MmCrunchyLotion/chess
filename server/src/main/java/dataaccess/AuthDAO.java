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
        AuthData token = new AuthData(auth.getUsername());
        tokens.addToken(token);
        return token;
    }

    public AuthData getAuth(AuthData auth) {
//        Find an auth from the DB
//        Return auth
        for (AuthData token : tokens.getTokens()) {
            if (token.getAuthToken().equals(auth.getAuthToken())) {
                return auth;
            }
        }
        return null;
    }

    public void removeAuth(AuthData auth) {
//        remove auth from DB
        tokens.removeToken(auth);
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

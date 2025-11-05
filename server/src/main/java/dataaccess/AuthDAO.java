package dataaccess;

import models.*;

public interface AuthDAO {

    static AuthData addAuth(AuthData auth) throws DataAccessException {
//        Add auth to DB
//        Return auth
        throw new DataAccessException("AddAuth called. No DB reached");
    }

    static AuthData getAuth(AuthData auth) throws DataAccessException {
//        Find an auth from the DB
//        Return auth
        throw new DataAccessException("GetAuth called. No DB reached");
    }

    static void removeAuth(AuthData auth) throws DataAccessException {
//        remove auth from DB
        throw new DataAccessException("RemoveAuth called. No DB reached");
    }
}

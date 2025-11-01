package dataaccess;

import models.*;

public interface AuthDAO {

    static AuthData addAuth(AuthData auth) throws DataAccessException {
//        Add auth to DB
//        Return auth
        throw new DataAccessException("AddAuth called. No DB reached");
    }
}

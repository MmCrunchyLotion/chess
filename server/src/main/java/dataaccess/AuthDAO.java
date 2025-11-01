package dataaccess;

import models.*;

public interface AuthDAO {

    static AuthData addAuth(AuthData auth, UserData user) throws DataAccessException {
        throw new DataAccessException("AddAuth called. No DB reached");
    }
}

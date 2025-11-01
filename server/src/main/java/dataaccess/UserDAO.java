package dataaccess;

import models.*;

public interface UserDAO {

    static UserData getUser(String username) throws DataAccessException {
//        Search database for user with username
//        return user;
        throw new DataAccessException("GetUser called. No DB reached");
    }

    static void createUser(UserData user) throws DataAccessException {
//        Add new user to the database
        throw new DataAccessException("CreateUser called. No DB reached");
    }
}

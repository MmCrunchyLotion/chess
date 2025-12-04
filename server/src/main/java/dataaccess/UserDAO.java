package dataaccess;

import mockdatabase.*;
import models.*;

public class UserDAO {

    private final Users users;

    public UserDAO() {
        this.users = new Users();
    }

    public UserData getUser(String username) {
//        Search database for user with username
//        return user;
        for (UserData user : users.getUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void createUser(UserData user) {
//        Add new user to the database
        users.addUser(user);
    }

    public void clear() {
//        Clear all Users
        users.clearUsers();
    }
}

//public interface UserDAO {
//
//    static UserData getUser(String username) throws DataAccessException {
////        Search database for user with username
////        return user;
//        throw new DataAccessException("GetUser called. No DB reached");
//    }
//
//    static void createUser(UserData user) throws DataAccessException {
////        Add new user to the database
//        throw new DataAccessException("CreateUser called. No DB reached");
//    }
//
//    static void clear() throws DataAccessException {
////        Clear all Users
//        throw new DataAccessException("UserClear called. No DB reached");
//    }
//}

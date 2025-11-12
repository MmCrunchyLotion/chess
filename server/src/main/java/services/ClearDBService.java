package services;

import dataaccess.*;
import dataaccess.DataAccessException;

public class ClearDBService {

    public ClearDBService(AuthDAO mockAuthDAO, UserDAO mockUserDAO, GameDAO mockGameDAO) {
//        Clear the database
        mockAuthDAO.clear();
        mockUserDAO.clear();
        mockGameDAO.clear();
    }
}

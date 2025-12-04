package services;

import dataaccess.*;

public class ClearDBService {

    public ClearDBService(AuthDAO mockAuthDAO, UserDAO mockUserDAO, GameDAO mockGameDAO) {
//        Clear the database
        mockAuthDAO.clear();
        mockUserDAO.clear();
        mockGameDAO.clear();
    }
}

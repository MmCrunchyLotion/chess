package services;

import dataaccess.*;
import dataaccess.DataAccessException;

public class ClearDBService {

    public ClearDBService() throws DataAccessException {
//        Clear the database
        AuthDAO.clear();
        GameDAO.clear();
        UserDAO.clear();
    }
}

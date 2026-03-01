package passoff.server;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import models.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {

    private static GameDAO gameDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDAO = new GameDAO();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clear();
    }

    // createGame
    @Test
    @DisplayName("Create Game - Success")
    void createGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("testGame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
        assertEquals("testGame", result.getGameName());
    }

    @Test
    @DisplayName("Create Game - Null Name")
    void createGameNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    // getGame
    @Test
    @DisplayName("Find Game - Success")
    void getGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("testGame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
        assertEquals(gameID, result.getGameID());
    }

    @Test
    @DisplayName("Find Game - Does Not Exist")
    void getGameNegative() throws DataAccessException {
        GameData result = gameDAO.getGame(-1);
        assertNull(result);
    }

    // getDBGames
    @Test
    @DisplayName("Get All Games - Success")
    void getDBGamesPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        var games = gameDAO.getDBGames();
        assertNotNull(games);
        assertEquals(3, games.size());
    }

    @Test
    @DisplayName("Get All Games - Empty Table")
    void getDBGamesNegative() throws DataAccessException {
        var games = gameDAO.getDBGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    // setUser
    @Test
    @DisplayName("Set User - Success")
    void setUserPositive() throws DataAccessException {
        // need a user in the db first since game references user via foreign key
        dataaccess.UserDAO userDAO = new dataaccess.UserDAO();
        userDAO.createUser(new models.UserData("testUser", "password", "test@mail.com"));
        int gameID = gameDAO.createGame("testGame");
        gameDAO.setUser("testUser", "WHITE", gameID);
        GameData result = gameDAO.getGame(gameID);
        assertEquals("testUser", result.getWhiteUsername());
    }

    @Test
    @DisplayName("Set User - User Does Not Exist")
    void setUserNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.setUser("nonexistentUser", "WHITE", 1));
    }

    // clear
    @Test
    @DisplayName("Clear - Success")
    void clearPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.clear();
        var games = gameDAO.getDBGames();
        assertEquals(0, games.size());
    }

    @Test
    @DisplayName("Clear - Empty Table")
    void clearNegative() {
        assertDoesNotThrow(() -> gameDAO.clear());
    }
}
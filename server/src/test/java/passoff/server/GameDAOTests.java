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
    void getAllGamesPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        var games = gameDAO.getAllGames();
        assertNotNull(games);
        assertEquals(3, games.size());
    }

    @Test
    @DisplayName("Get All Games - Empty Table")
    void getAllGamesNegative() throws DataAccessException {
        var games = gameDAO.getAllGames();
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

    // Game State

    @Test
    @DisplayName("Game State - Initial Board is Valid")
    void gameStateInitialBoard() throws DataAccessException {
        // a newly created game should have a valid starting chess board
        int gameID = gameDAO.createGame("testGame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result.getGame());
        // white should go first
        assertEquals(chess.ChessGame.TeamColor.WHITE, result.getGame().getTeamTurn());
        // board should have 32 pieces in starting position
        long pieceCount = result.getGame().getBoard().getBoard().stream()
                .filter(pos -> pos.getOccupied() != null)
                .count();
        assertEquals(32, pieceCount);
    }

    @Test
    @DisplayName("Game State - Update and Retrieve")
    void gameStateUpdateAndRetrieve() throws DataAccessException {
        // updating the game state should persist correctly to the database
        int gameID = gameDAO.createGame("testGame");
        GameData result = gameDAO.getGame(gameID);
        // make a move and update
        chess.ChessGame game = result.getGame();
        game.setTeamTurn(chess.ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameID, game);
        // retrieve and verify the update persisted
        GameData updated = gameDAO.getGame(gameID);
        assertEquals(chess.ChessGame.TeamColor.BLACK, updated.getGame().getTeamTurn());
    }

    @Test
    @DisplayName("Game State - Serialization Round Trip")
    void gameStateSerializationRoundTrip() throws DataAccessException {
        // serializing and deserializing a game should produce an equal game object
        int gameID = gameDAO.createGame("testGame");
        GameData original = gameDAO.getGame(gameID);
        chess.ChessGame originalGame = original.getGame();
        // update with the same game to force a serialize/deserialize cycle
        gameDAO.updateGame(gameID, originalGame);
        GameData retrieved = gameDAO.getGame(gameID);
        // board state should be identical after round trip
        assertEquals(originalGame.getBoard(), retrieved.getGame().getBoard());
        assertEquals(originalGame.getTeamTurn(), retrieved.getGame().getTeamTurn());
    }

    @Test
    @DisplayName("Game State - Persists After Recreating DAO")
    void gameStatePersistsAfterRestart() throws DataAccessException {
        // game state should still be retrievable after creating a new DAO instance
        // simulates a server restart since the DAO is recreated
        int gameID = gameDAO.createGame("testGame");
        GameData original = gameDAO.getGame(gameID);
        chess.ChessGame game = original.getGame();
        game.setTeamTurn(chess.ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(gameID, game);
        // create a brand new DAO instance (simulates restart)
        GameDAO newGameDAO = new GameDAO();
        GameData result = newGameDAO.getGame(gameID);
        assertNotNull(result);
        assertNotNull(result.getGame());
        // team turn should still be BLACK after the simulated restart
        assertEquals(chess.ChessGame.TeamColor.BLACK, result.getGame().getTeamTurn());
    }

    // clear
    @Test
    @DisplayName("Clear - Success")
    void clearPositive() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.clear();
        var games = gameDAO.getAllGames();
        assertEquals(0, games.size());
    }

    @Test
    @DisplayName("Clear - Empty Table")
    void clearNegative() {
        assertDoesNotThrow(() -> gameDAO.clear());
    }
}
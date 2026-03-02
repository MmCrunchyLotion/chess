package client;

import exception.ResponseException;
import models.*;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // register
    @Test
    @DisplayName("Register - Success")
    void registerPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertNotNull(auth);
        assertTrue(auth.getAuthToken().length() > 10);
        assertEquals("player1", auth.getUsername());
    }

    @Test
    @DisplayName("Register - Duplicate Username")
    void registerNegative() throws ResponseException {
        facade.register(new UserData("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () ->
                facade.register(new UserData("player1", "password2", "p2@email.com")));
    }

    // login
    @Test
    @DisplayName("Login - Success")
    void loginPositive() throws ResponseException {
        facade.register(new UserData("player1", "password", "p1@email.com"));
        AuthData auth = facade.login(new UserData("player1", "password", null));
        assertNotNull(auth);
        assertNotNull(auth.getAuthToken());
        assertEquals("player1", auth.getUsername());
    }

    @Test
    @DisplayName("Login - Wrong Password")
    void loginNegative() throws ResponseException {
        facade.register(new UserData("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () ->
                facade.login(new UserData("player1", "wrongPassword", null)));
    }

    // logout
    @Test
    @DisplayName("Logout - Success")
    void logoutPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertDoesNotThrow(() -> facade.logout(auth.getAuthToken()));
    }

    @Test
    @DisplayName("Logout - Invalid Token")
    void logoutNegative() {
        assertThrows(ResponseException.class, () -> facade.logout("invalidToken"));
    }

    // createGame
    @Test
    @DisplayName("Create Game - Success")
    void createGamePositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        GameData game = facade.createGame("testGame", auth.getAuthToken());
        assertNotNull(game);
        assertTrue(game.getGameID() > 0);
    }

    @Test
    @DisplayName("Create Game - Invalid Auth")
    void createGameNegative() {
        assertThrows(ResponseException.class, () ->
                facade.createGame("testGame", "invalidToken"));
    }

    // listGames
    @Test
    @DisplayName("List Games - Success")
    void listGamesPositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        facade.createGame("game1", auth.getAuthToken());
        facade.createGame("game2", auth.getAuthToken());
        GameData[] games = facade.listGames(auth.getAuthToken());
        assertNotNull(games);
        assertEquals(2, games.length);
    }

    @Test
    @DisplayName("List Games - Invalid Auth")
    void listGamesNegative() {
        assertThrows(ResponseException.class, () -> facade.listGames("invalidToken"));
    }

    // joinGame
    @Test
    @DisplayName("Join Game - Success")
    void joinGamePositive() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        GameData game = facade.createGame("testGame", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame("WHITE", game.getGameID(), auth.getAuthToken()));
    }

    @Test
    @DisplayName("Join Game - Color Already Taken")
    void joinGameNegative() throws ResponseException {
        AuthData auth1 = facade.register(new UserData("player1", "password", "p1@email.com"));
        AuthData auth2 = facade.register(new UserData("player2", "password", "p2@email.com"));
        GameData game = facade.createGame("testGame", auth1.getAuthToken());
        facade.joinGame("WHITE", game.getGameID(), auth1.getAuthToken());
        assertThrows(ResponseException.class, () ->
                facade.joinGame("WHITE", game.getGameID(), auth2.getAuthToken()));
    }

    @Test
    @DisplayName("Join Game - Same User Both Colors")
    void joinGameSameUserBothColors() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        GameData game = facade.createGame("testGame", auth.getAuthToken());
        facade.joinGame("WHITE", game.getGameID(), auth.getAuthToken());
        // same user trying to join as black should throw
        assertThrows(ResponseException.class, () ->
                facade.joinGame("BLACK", game.getGameID(), auth.getAuthToken()));
    }

    // rejoin game
    @Test
    @DisplayName("Rejoin Game - Same Color Success")
    void rejoinGameSameColor() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        GameData game = facade.createGame("testGame", auth.getAuthToken());
        facade.joinGame("WHITE", game.getGameID(), auth.getAuthToken());
        // should be able to rejoin as the same color
        assertDoesNotThrow(() -> facade.joinGame("WHITE", game.getGameID(), auth.getAuthToken()));
    }

    @Test
    @DisplayName("Rejoin Game - Switch Color Blocked")
    void rejoinGameSwitchColor() throws ResponseException {
        AuthData auth = facade.register(new UserData("player1", "password", "p1@email.com"));
        GameData game = facade.createGame("testGame", auth.getAuthToken());
        facade.joinGame("WHITE", game.getGameID(), auth.getAuthToken());
        // should not be able to switch to the other color
        assertThrows(ResponseException.class, () ->
                facade.joinGame("BLACK", game.getGameID(), auth.getAuthToken()));
    }
}
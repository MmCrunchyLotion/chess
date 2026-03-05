package service;

import dataaccess.*;
import exception.ResponseException;

import models.*;
import org.junit.jupiter.api.*;
import services.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllServiceTests {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static UserData testUser;

    @BeforeAll
    static void setup() throws DataAccessException {
        // instantiate DAOs once — user must come before game due to foreign key constraint
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
    }

    @BeforeEach
    void clear() throws ResponseException {
        // wipe all tables before each test so tests don't interfere with each other
        new ClearDBService(authDAO, userDAO, gameDAO);
        testUser = new UserData("testUser", "password", "test@mail.com");
    }

    // helper to register and login, returning a valid auth token
    private AuthData registerAndLogin() throws ResponseException {
        new RegisterUserService(testUser, userDAO, authDAO).register();
        LoginService loginService = new LoginService(testUser, userDAO, authDAO);
        loginService.login();
        return loginService.getAuth();
    }

    // ### RegisterUserService ###

    @Test
    @Order(1)
    @DisplayName("Register - Success")
    void registerPositive() throws ResponseException {
        // registering a new user should succeed and return a valid auth token
        RegisterUserService service = new RegisterUserService(testUser, userDAO, authDAO);
        service.register();
        AuthData auth = service.getAuth();
        assertNotNull(auth);
        assertNotNull(auth.getAuthToken());
        assertEquals(testUser.getUsername(), auth.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Register - Duplicate Username")
    void registerNegative() throws ResponseException {
        // registering the same username twice should throw AlreadyTaken
        new RegisterUserService(testUser, userDAO, authDAO).register();
        RegisterUserService duplicate = new RegisterUserService(testUser, userDAO, authDAO);
        ResponseException ex = assertThrows(ResponseException.class, duplicate::register);
        assertEquals(ResponseException.Code.AlreadyTaken, ex.code());
    }

    // ### LoginService ###

    @Test
    @Order(3)
    @DisplayName("Login - Success")
    void loginPositive() throws ResponseException {
        // logging in with correct credentials should return a valid auth token
        new RegisterUserService(testUser, userDAO, authDAO).register();
        LoginService service = new LoginService(testUser, userDAO, authDAO);
        service.login();
        AuthData auth = service.getAuth();
        assertNotNull(auth);
        assertNotNull(auth.getAuthToken());
        assertEquals(testUser.getUsername(), auth.getUsername());
    }

    @Test
    @Order(4)
    @DisplayName("Login - Wrong Password")
    void loginNegative() throws ResponseException {
        // logging in with the wrong password should throw Unauthorized
        new RegisterUserService(testUser, userDAO, authDAO).register();
        UserData wrongPassword = new UserData("testUser", "wrongPassword", "test@mail.com");
        LoginService service = new LoginService(wrongPassword, userDAO, authDAO);
        ResponseException ex = assertThrows(ResponseException.class, service::login);
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    // ### LogoutService ###

    @Test
    @Order(5)
    @DisplayName("Logout - Success")
    void logoutPositive() throws ResponseException {
        // logging out with a valid auth token should succeed without throwing
        AuthData auth = registerAndLogin();
        LogoutService logoutService = new LogoutService(auth, authDAO);
        assertDoesNotThrow(logoutService::logout);
    }

    @Test
    @Order(6)
    @DisplayName("Logout - Invalid Auth Token")
    void logoutNegative() {
        // logging out with an invalid auth token should throw Unauthorized
        AuthData badAuth = new AuthData("testUser", "invalidToken");
        ResponseException ex = assertThrows(ResponseException.class, () ->
                new LogoutService(badAuth, authDAO));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    // ### CreateGameService ###

    @Test
    @Order(7)
    @DisplayName("Create Game - Success")
    void createGamePositive() throws ResponseException {
        // creating a game with valid auth should return a game with a valid ID
        AuthData auth = registerAndLogin();
        GameData game = new GameData("testGame");
        CreateGameService service = new CreateGameService(auth, game, authDAO, gameDAO);
        GameData result = service.addGame();
        assertNotNull(result);
        assertTrue(result.getGameID() > 0);
        assertEquals("testGame", result.getGameName());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game - Invalid Auth")
    void createGameNegative() {
        // creating a game with an invalid auth token should throw Unauthorized
        AuthData badAuth = new AuthData("testUser", "invalidToken");
        GameData game = new GameData("testGame");
        ResponseException ex = assertThrows(ResponseException.class, () ->
                new CreateGameService(badAuth, game, authDAO, gameDAO));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    // ### ListGamesService ###

    @Test
    @Order(9)
    @DisplayName("List Games - Success")
    void listGamesPositive() throws ResponseException {
        // listing games with valid auth should return all created games
        AuthData auth = registerAndLogin();
        new CreateGameService(auth, new GameData("game1"), authDAO, gameDAO).addGame();
        new CreateGameService(auth, new GameData("game2"), authDAO, gameDAO).addGame();
        ListGamesService service = new ListGamesService(auth, authDAO, gameDAO);
        Collection<GameData> games = service.list();
        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    @Order(10)
    @DisplayName("List Games - Invalid Auth")
    void listGamesNegative() {
        // listing games with an invalid auth token should throw Unauthorized
        AuthData badAuth = new AuthData("testUser", "invalidToken");
        ResponseException ex = assertThrows(ResponseException.class, () ->
                new ListGamesService(badAuth, authDAO, gameDAO));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    // ### JoinGameService ###

    @Test
    @Order(11)
    @DisplayName("Join Game - Success")
    void joinGamePositive() throws ResponseException, DataAccessException {
        // joining a game as white should correctly set the white username
        AuthData auth = registerAndLogin();
        GameData createdGame = new CreateGameService(auth, new GameData("testGame"), authDAO, gameDAO).addGame();
        JoinBody join = new JoinBody("WHITE", createdGame.getGameID());
        JoinGameService joinService = new JoinGameService(auth, join, authDAO, gameDAO);
        assertDoesNotThrow(() -> joinService.addPlayer(gameDAO));
        // verify the username was actually set in the database
        GameData result = gameDAO.getGame(createdGame.getGameID());
        assertEquals(testUser.getUsername(), result.getWhiteUsername());
    }

    @Test
    @Order(12)
    @DisplayName("Join Game - Color Already Taken")
    void joinGameNegative() throws ResponseException {
        // joining a color that is already taken should throw AlreadyTaken
        AuthData auth = registerAndLogin();
        GameData createdGame = new CreateGameService(auth, new GameData("testGame"), authDAO, gameDAO).addGame();

        // first player joins as white
        JoinBody join = new JoinBody("WHITE", createdGame.getGameID());
        new JoinGameService(auth, join, authDAO, gameDAO).addPlayer(gameDAO);

        // second player tries to also join as white
        UserData secondUser = new UserData("secondUser", "password", "second@mail.com");
        new RegisterUserService(secondUser, userDAO, authDAO).register();
        LoginService secondLogin = new LoginService(secondUser, userDAO, authDAO);
        secondLogin.login();
        AuthData secondAuth = secondLogin.getAuth();
        JoinGameService secondJoin = new JoinGameService(secondAuth, join, authDAO, gameDAO);
        ResponseException ex = assertThrows(ResponseException.class, () -> secondJoin.addPlayer(gameDAO));
        assertEquals(ResponseException.Code.AlreadyTaken, ex.code());
    }

    // ### ClearDBService ###

    @Test
    @Order(13)
    @DisplayName("Clear - Success")
    void clearPositive() throws ResponseException, DataAccessException {
        // after clearing, all data should be gone
        new RegisterUserService(testUser, userDAO, authDAO).register();
        new ClearDBService(authDAO, userDAO, gameDAO);
        assertNull(userDAO.getUser(testUser.getUsername()));
    }

    @Test
    @Order(14)
    @DisplayName("Clear - Empty Tables")
    void clearNegative() {
        // clearing already empty tables should not throw
        assertDoesNotThrow(() -> new ClearDBService(authDAO, userDAO, gameDAO));
    }

    @Test
    @Order(15)
    @DisplayName("Join Game - Rejoin Same Color Success")
    void rejoinGameSameColorPositive() throws ResponseException, DataAccessException {
        // a player should be able to rejoin a game as the same color they already have
        AuthData auth = registerAndLogin();
        GameData createdGame = new CreateGameService(auth, new GameData("testGame"), authDAO, gameDAO).addGame();
        JoinBody join = new JoinBody("WHITE", createdGame.getGameID());
        new JoinGameService(auth, join, authDAO, gameDAO).addPlayer(gameDAO);
        // rejoin as white should succeed
        JoinGameService rejoin = new JoinGameService(auth, join, authDAO, gameDAO);
        assertDoesNotThrow(() -> rejoin.addPlayer(gameDAO));
        GameData result = gameDAO.getGame(createdGame.getGameID());
        assertEquals(testUser.getUsername(), result.getWhiteUsername());
    }

    @Test
    @Order(16)
    @DisplayName("Join Game - Other Player Color Blocked")
    void joinGameOtherPlayerColorNegative() throws ResponseException {
        // a second player should not be able to take a color already claimed by someone else
        AuthData auth1 = registerAndLogin();
        UserData secondUser = new UserData("secondUser", "password", "second@mail.com");
        new RegisterUserService(secondUser, userDAO, authDAO).register();
        LoginService secondLogin = new LoginService(secondUser, userDAO, authDAO);
        secondLogin.login();
        AuthData auth2 = secondLogin.getAuth();
        GameData createdGame = new CreateGameService(auth1, new GameData("testGame"), authDAO, gameDAO).addGame();
        JoinBody join = new JoinBody("WHITE", createdGame.getGameID());
        new JoinGameService(auth1, join, authDAO, gameDAO).addPlayer(gameDAO);
        // second player tries to take white
        JoinGameService secondJoin = new JoinGameService(auth2, join, authDAO, gameDAO);
        ResponseException ex = assertThrows(ResponseException.class, () -> secondJoin.addPlayer(gameDAO));
        assertEquals(ResponseException.Code.AlreadyTaken, ex.code());
    }
}
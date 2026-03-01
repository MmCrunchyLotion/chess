package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests {

    private static AuthDAO authDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        authDAO = new AuthDAO();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        authDAO.clear();
    }

    // AddAuth
    @Test
    @DisplayName("Add Auth - Success")
    void addAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("testUser", null);
        authDAO.addAuth(auth);
        AuthData result = authDAO.getAuthByToken(auth.getAuthToken());
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    @DisplayName("Add Auth - Null Username")
    void addAuthNegative() {
        assertThrows(DataAccessException.class, () ->
                authDAO.addAuth(new AuthData(null, "someToken")));
    }

    // getAuthByToken
    @Test
    @DisplayName("Get Auth By Token - Success")
    void getAuthByTokenPositive() throws DataAccessException {
        AuthData auth = new AuthData("testUser", null);
        authDAO.addAuth(auth);
        AuthData result = authDAO.getAuthByToken(auth.getAuthToken());
        assertNotNull(result);
        assertEquals(auth.getAuthToken(), result.getAuthToken());
    }

    @Test
    @DisplayName("Get Auth By Token - Token Does Not Exist")
    void getAuthByTokenNegative() throws DataAccessException {
        AuthData result = authDAO.getAuthByToken("nonexistentToken");
        assertNull(result);
    }

    // removeAuth
    @Test
    @DisplayName("Remove Auth - Success")
    void removeAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("testUser", null);
        authDAO.addAuth(auth);
        authDAO.removeAuth(auth.getAuthToken());
        AuthData result = authDAO.getAuthByToken(auth.getAuthToken());
        assertNull(result);
    }

    @Test
    @DisplayName("Remove Auth - Token Does Not Exist")
    void removeAuthNegative() throws DataAccessException {
        // removing a token that doesn't exist should not throw
        assertDoesNotThrow(() -> authDAO.removeAuth("nonexistentToken"));
    }

    // clear
    @Test
    @DisplayName("Clear - Success")
    void clearPositive() throws DataAccessException {
        authDAO.addAuth(new AuthData("user1", "test1"));
        authDAO.addAuth(new AuthData("user2", "test2"));
        authDAO.clear();
        // after clear, any token lookup should return null
        assertNull(authDAO.getAuthByToken("test1"));
        assertNull(authDAO.getAuthByToken("test2"));
    }

    @Test
    @DisplayName("Clear - Empty Table")
    void clearNegative() {
        // clearing an already empty table should not throw
        assertDoesNotThrow(() -> authDAO.clear());
    }
}

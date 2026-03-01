package passoff.server;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import models.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {

    private static UserDAO userDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        userDAO = new UserDAO();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        userDAO.clear();
    }

    // AddUser
    @Test
    @DisplayName("Create User - Success")
    void addUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email");
        userDAO.createUser(user);
        UserData result = userDAO.getUser(user.getUsername());
        assertNotNull(result);
        assertTrue(userDAO.verifyPassword(result.getUsername(), "password"));
        assertEquals(result.getEmail(), "email");
        assertEquals(result.getUsername(), "testUser");
    }

    @Test
    @DisplayName("Add User - Null Username")
    void addUserNegative1() {
        // null username violates the NOT NULL constraint in the database
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData(null, "password", "email")));
    }

    @Test
    @DisplayName("Add User - Null Password")
    void addUserNegative2() {
        // null password violates the NOT NULL constraint in the database
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData("testUser", null, "email")));
    }

    // getUser
    @Test
    @DisplayName("Get User - Success")
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email");
        userDAO.createUser(user);
        UserData result = userDAO.getUser(user.getUsername());
        assertNotNull(result);
        assertTrue(userDAO.verifyPassword(result.getUsername(), "password"));
        assertEquals(result.getEmail(), "email");
        assertEquals(result.getUsername(), "testUser");
    }

    @Test
    @DisplayName("Get User - User Does Not Exist")
    void getUserNegative() throws DataAccessException {
        // looking up a nonexistent user should return null rather than throwing
        UserData result = userDAO.getUser("nonexistentUser");
        assertNull(result);
    }

    // clear
    @Test
    @DisplayName("Clear - Success")
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("testUser1", "password", "email"));
        userDAO.createUser(new UserData("testUser2", "password", "email"));
        userDAO.clear();
        // both users should be gone after clear
        assertNull(userDAO.getUser("testUser1"));
        assertNull(userDAO.getUser("testUser2"));
    }

    @Test
    @DisplayName("Clear - Empty Table")
    void clearNegative() {
        // clearing an already empty table should succeed without throwing
        assertDoesNotThrow(() -> userDAO.clear());
    }
}
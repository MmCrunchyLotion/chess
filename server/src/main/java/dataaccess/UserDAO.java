package dataaccess;

import models.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO extends MySqlDataAccess {

    public UserDAO() throws DataAccessException{
        String[] createStatements = {
        """
            CREATE TABLE IF NOT EXISTS user (
            id int NOT NULL AUTO_INCREMENT,
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) DEFAULT NULL,
            PRIMARY KEY (id),
            INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """
        };
        configureDatabase(createStatements);
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get user: " + e.getMessage());
        }
        return null;
    }

    public boolean verifyPassword(String username, String plainPassword) throws DataAccessException {
        UserData user = getUser(username);
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPassword());
    }

    public void createUser(UserData user) throws DataAccessException {
        if (user.getPassword() == null || user.getUsername() == null) {
            throw new DataAccessException("Error: username and password cannot be null");
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sql, user.getUsername(), hashedPassword, user.getEmail());
    }

    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM user");
    }
}

package dataaccess;

import models.AuthData;
import java.sql.*;

public class AuthDAO extends MySqlDataAccess {

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS auth (
            id int NOT NULL AUTO_INCREMENT,
            username varchar(256) NOT NULL,
            token varchar(256) NOT NULL,
            PRIMARY KEY (id),
            INDEX(username),
            INDEX(token)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    public AuthDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    public void addAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (username, token) VALUES (?, ?)";
        executeUpdate(sql, auth.getUsername(), auth.getAuthToken());
    }

    public AuthData getAuthByToken(String token) throws DataAccessException {
        String sql = "SELECT username, token FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("username"), rs.getString("token"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get auth token: " + e.getMessage());
        }
        return null;
    }

    public void removeAuth(String token) throws DataAccessException {
        executeUpdate("DELETE FROM auth WHERE token = ?", token);
    }

    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE auth");
    }
}
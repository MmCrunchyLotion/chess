package dataaccess;

import exception.ResponseException;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class MySqlDataAccess {

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param == null) ps.setNull(i + 1, NULL);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database: " + e.getMessage());
        }
    }

    protected void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("unable to configure database: " + ex.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
                id int NOT NULL AUTO_INCREMENT,
                whiteUsername int DEFAULT NULL,
                blackUsername int DEFAULT NULL,
                gameName varchar(256) NOT NULL,
                PRIMARY KEY ('id'),
                CONSTRAINT fk_white FOREIGN KEY (whiteUsername) REFERENCES user (id),
                CONSTRAINT fk_black FOREIGN KEY (blackUsername) REFERENCES user (id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

            CREATE TABLE IF NOT EXISTS auth (
                id int NOT NULL AUTO_INCREMENT,
                username varchar(256) NOT NULL,
                token varchar(256) NOT NULL,
                PRIMARY KEY ('id'),
                INDEX(username),
                INDEX(token)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """
    };

}
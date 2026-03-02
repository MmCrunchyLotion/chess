package dataaccess;

import models.GameData;
import com.google.gson.Gson;
import chess.ChessGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class GameDAO extends MySqlDataAccess {

    public GameDAO() throws DataAccessException {
        String[] createStatements = {
        """
            CREATE TABLE IF NOT EXISTS game (
                id int NOT NULL AUTO_INCREMENT,
                whiteUserID int DEFAULT NULL,
                blackUserID int DEFAULT NULL,
                gameName varchar(256) NOT NULL,
                gameState TEXT NOT NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_white FOREIGN KEY (whiteUserID) REFERENCES user (id),
                CONSTRAINT fk_black FOREIGN KEY (blackUserID) REFERENCES user (id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
        };
        configureDatabase(createStatements);
    }

    private static final Gson GSON = new Gson();

    public int createGame(String gameName) throws DataAccessException {
        String gameState = GSON.toJson(new ChessGame());
        String sql = "INSERT INTO game (gameName, gameState) VALUES (?, ?)";
        return executeUpdate(sql, gameName, gameState);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String sql =
            """
                SELECT g.id, g.gameName, g.gameState,
                       white.username AS whiteUsername,
                       black.username AS blackUsername
                FROM game g
                LEFT JOIN user white ON g.whiteUserID = white.id
                LEFT JOIN user black ON g.blackUserID = black.id
                WHERE g.id = ?
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildGameData(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to find game: " + e.getMessage());
        }
        return null;
    }

    public Collection<GameData> getAllGames() throws DataAccessException {
        String sql =
            """
                SELECT g.id, g.gameName, g.gameState,
                       white.username AS whiteUsername,
                       black.username AS blackUsername
                FROM game g
                LEFT JOIN user white ON g.whiteUserID = white.id
                LEFT JOIN user black ON g.blackUserID = black.id
            """;
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(buildGameData(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get games: " + e.getMessage());
        }
        return games;
    }

    public void setUser(String username, String color, int gameID) throws DataAccessException {
        String getUserID = "SELECT id FROM user WHERE username = ?";
        int userID = getUserID(username, getUserID);
        String column = color.equals("WHITE") ? "whiteUserID" : "blackUserID";
        String sql = "UPDATE game SET " + column + " = ? WHERE id = ?";
        executeUpdate(sql, userID, gameID);
    }

    private static int getUserID(String username, String getUserID) throws DataAccessException {
        int userID;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUserID)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DataAccessException("Error: user not found: " + username);
                userID = rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get user ID: " + e.getMessage());
        }
        return userID;
    }

    private GameData buildGameData(ResultSet rs) throws SQLException {
        GameData game = new GameData(rs.getString("gameName"));
        game.setGameID(rs.getInt("id"));
        game.setWhiteUsername(rs.getString("whiteUsername"));
        game.setBlackUsername(rs.getString("blackUsername"));
        game.setGame(GSON.fromJson(rs.getString("gameState"), ChessGame.class));
        return game;
    }

    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        String gameState = GSON.toJson(chessGame);
        String sql = "UPDATE game SET gameState = ? WHERE id = ?";
        executeUpdate(sql, gameState, gameID);
    }

    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE game");
    }
}
package models;

import chess.ChessGame;

public class GameData {

    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private ChessGame game;

    public GameData(String gameName) {
        this.gameName = gameName;
        this.game = new ChessGame();
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }
}

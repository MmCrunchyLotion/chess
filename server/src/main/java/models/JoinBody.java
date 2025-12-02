package models;

import com.google.gson.Gson;

public class JoinBody {

    private String playerColor;
    private int gameID;

    public JoinBody(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

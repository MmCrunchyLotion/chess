package models;

import com.google.gson.Gson;

public class JoinBody {

//    private String authToken;
    private String playerColor;
    private int gameID;

    public JoinBody(String authToken, String playerColor, int gameID) {
//        this.authToken = authToken;
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

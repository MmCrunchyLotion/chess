package mockdatabase;

import models.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class Games {

    private Collection<GameData> games;

    public Games() {
        this.games = new ArrayList<>();
    }

    public Collection<GameData> getGames() {
        return games;
    }

    public int addGame(GameData game) {
        game.setGameID(games.size());
        games.add(game);
        return game.getGameID();
    }

    public GameData findGame(int gameID) {
        for (GameData game : games) {
            if (gameID == game.getGameID()) {
                return game;
            }
        }
        return null;
    }

    public void clearGames() {
        games.clear();
    }
}

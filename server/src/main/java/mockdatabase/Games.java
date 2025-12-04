package mockdatabase;

import java.util.ArrayList;
import java.util.Collection;
import models.GameData;

public class Games {

    private final Collection<GameData> games;

    public Games() {
        this.games = new ArrayList<>();
    }

    public Collection<GameData> getGames() {
        return games;
    }

    public int addGame(GameData game) {
        game.setGameID(games.size() + 1);
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

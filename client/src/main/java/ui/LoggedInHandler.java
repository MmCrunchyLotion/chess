package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.WebSocketFacade;
import exception.ResponseException;
import models.AuthData;
import models.GameData;

public class LoggedInHandler extends Handler {

    private final ServerFacade server;
    private UILoop.States state;
    private GameData[] lastGameList = new GameData[0];
    private final PlayingHandler[] playingHandler;
    private final SpectatingHandler[] spectatingHandler;

    public LoggedInHandler(ServerFacade facade, PlayingHandler[] playingHandler, SpectatingHandler[] spectatingHandler) {
        this.server = facade;
        this.playingHandler = playingHandler;
        this.spectatingHandler = spectatingHandler;
        this.state = UILoop.States.LOGGED_IN;
    }

    public void handle(String[] args, AuthData auth) throws ResponseException {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "logout" -> this.state = logout(args, auth);
            case "create" -> create(args, auth);
            case "list" -> list(args, auth);
            case "join" -> this.state = join(args, auth);
            case "observe" -> this.state = observe(args, auth);
            default -> System.out.println("Unknown command. Type 'help' for a list of commands.\n");
        }
        clearArgs();
    }

    private static void help() {
        System.out.println("Available commands:");
        System.out.println("  help                              - shows possible commands");
        System.out.println("  logout                            - logout current user");
        System.out.println("  create <NAME>                     - create a new chess game");
        System.out.println("  list                              - list existing chess games");
        System.out.println("  join <ID> [WHITE|BLACK]           - join a game as a player");
        System.out.println("  observe <ID>                       - join a game as spectator");
        System.out.println("  quit                              - exit the client\n");
    }

    public UILoop.States logout(String[] args, AuthData auth) throws ResponseException {
        if (args.length != 1) {
            System.out.println("Usage: logout\n");
            return UILoop.States.LOGGED_IN;
        }
        server.logout(auth.getAuthToken());
        System.out.println("Logged out\n");
        return UILoop.States.LOGGED_OUT;
    }

    private void create(String[] args, AuthData auth) throws ResponseException {
        if (args.length != 2) {
            System.out.println("Usage: create <NAME>\n");
        } else {
            GameData game = server.createGame(arg1, auth.getAuthToken());
            System.out.println("Created game '" + arg1 + "' with ID: " + game.getGameID() + "\n");
        }
    }

    private void list(String[] args, AuthData auth) throws ResponseException {
        if (args.length != 1) {
            System.out.println("Usage: list\n");
        } else {
            lastGameList = server.listGames(auth.getAuthToken());
            if (lastGameList.length == 0) {
                System.out.println("No games available\n");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < lastGameList.length; i++) {
                    GameData game = lastGameList[i];
                    System.out.printf("  %d. %s | White: %s | Black: %s%n",
                            i + 1,
                            game.getGameName(),
                            game.getWhiteUsername() != null ? game.getWhiteUsername() : "empty",
                            game.getBlackUsername() != null ? game.getBlackUsername() : "empty");
                }
                System.out.println();
            }
        }
    }

    private UILoop.States join(String[] args, AuthData auth) throws ResponseException {
        if (args.length != 3) {
            System.out.println("Usage: join <NUMBER> [WHITE|BLACK]\n");
            return UILoop.States.LOGGED_IN;
        }
        try {
            int listNumber = Integer.parseInt(arg1);
            if (lastGameList.length == 0) {
                System.out.println("Please run 'list' first\n");
                return UILoop.States.LOGGED_IN;
            }
            if (listNumber < 1 || listNumber > lastGameList.length) {
                System.out.println("Invalid game number\n");
                return UILoop.States.LOGGED_IN;
            }

            String username = auth.getUsername();
            String color = arg2.toUpperCase();
            GameData game = lastGameList[listNumber - 1];

            if (color.equals("WHITE") && username.equals(game.getBlackUsername())) {
                System.out.println("You are already playing as Black in this game\n");
                return UILoop.States.LOGGED_IN;
            }
            if (color.equals("BLACK") && username.equals(game.getWhiteUsername())) {
                System.out.println("You are already playing as White in this game\n");
                return UILoop.States.LOGGED_IN;
            }

            server.joinGame(color, game.getGameID(), auth.getAuthToken());
            ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            WebSocketFacade ws = new WebSocketFacade(server.getServerUrl(), null);
            playingHandler[0] = new PlayingHandler(ws, auth, game.getGameID(), teamColor);
            ws.setMessageHandler(playingHandler[0]);
            ws.connect(auth.getAuthToken(), game.getGameID());

            System.out.println("Joined game " + listNumber + " as " + color + "\n");
            return UILoop.States.PLAYING;
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number, must be a number\n");
            return UILoop.States.LOGGED_IN;
        }
    }

    private UILoop.States observe(String[] args, AuthData auth) throws ResponseException {
        if (args.length != 2) {
            System.out.println("Usage: observe <NUMBER>\n");
            return UILoop.States.LOGGED_IN;
        }
        try {
            int listNumber = Integer.parseInt(arg1);
            if (lastGameList.length == 0) {
                System.out.println("Please run 'list' first\n");
                return UILoop.States.LOGGED_IN;
            }
            if (listNumber < 1 || listNumber > lastGameList.length) {
                System.out.println("Invalid game number\n");
                return UILoop.States.LOGGED_IN;
            }

            GameData game = lastGameList[listNumber - 1];
            WebSocketFacade ws = new WebSocketFacade(server.getServerUrl(), null);
            spectatingHandler[0] = new SpectatingHandler(ws, auth, game.getGameID());
            ws.setMessageHandler(spectatingHandler[0]);
            ws.connect(auth.getAuthToken(), game.getGameID());

            System.out.println("Spectating game " + listNumber + "\n");
            return UILoop.States.SPECTATING;
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number, must be a number\n");
            return UILoop.States.LOGGED_IN;
        }
    }

    private Integer getListNumber() {
        int listNumber = Integer.parseInt(arg1);
        if (lastGameList.length == 0) {
            System.out.println("Please run 'list' first to see available games\n");
            return null;
        }
        if (listNumber < 1 || listNumber > lastGameList.length) {
            System.out.println("Invalid game number, please choose between 1 and " + lastGameList.length + "\n");
            return null;
        }
        return listNumber;
    }

    public UILoop.States getState() {
        return state;
    }

    public void setState(UILoop.States state) {
        this.state = state;
    }
}
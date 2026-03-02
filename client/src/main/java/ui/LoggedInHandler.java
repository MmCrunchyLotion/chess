package ui;

import client.ServerFacade;
import exception.ResponseException;
import models.AuthData;
import models.GameData;

public class LoggedInHandler extends Handler {

    private final ServerFacade server;
    private AuthData auth;
    private UILoop.States state;

    public LoggedInHandler(ServerFacade facade) {
        this.server = facade;
        this.auth = null;
        this.state = UILoop.States.LOGGED_IN;
    }

    public void handle(String[] args, AuthData auth) throws ResponseException {
        setArgs(args);
        this.auth = auth;
        switch (arg0) {
            case "help" -> help();
            case "logout" -> logout(args);
            case "create" -> create(args);
            case "list" -> list(args);
            case "join" -> this.state = join(args);
            default -> System.out.println("Invalid command\n");
        }
        clearArgs();
    }

    private static void help() {
        System.out.println("help - shows possible commands\n");
        System.out.println("logout - logout current user\n");
        System.out.println("create <GAMENAME> - create a new chess game\n");
        System.out.println("list -  list existing chess games\n");
        System.out.println("join <MODE> <GAMENAME> <COLOR> - join a chess game with mode -s for spectator, -p for playing\n");
        System.out.println("exit - exit the client\n");
    }

    public UILoop.States logout(String[] args) throws ResponseException {
        if (args.length != 1) {
            System.out.println("Incorrect number of parameters\n");
            return UILoop.States.LOGGED_IN;
        } else {
            server.logout(this.auth.getAuthToken());
            this.auth = null;
            return UILoop.States.LOGGED_OUT;
        }
    }

    private void create(String[] args) throws ResponseException {
        if (args.length != 2) {
            System.out.println("Incorrect number of parameters\n");
        } else {
            GameData game = server.createGame(arg1, this.auth.getAuthToken());
            System.out.println("Created game '" + arg1 + "' with ID: " + game.getGameID() + "\n");
        }
    }

    private void list(String[] args) throws ResponseException {
        if (args.length != 1) {
            System.out.println("Incorrect number of parameters\n");
        } else {
            GameData[] games = server.listGames(this.auth.getAuthToken());
            if (games.length == 0) {
                System.out.println("No games available\n");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < games.length; i++) {
                    GameData game = games[i];
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

    private UILoop.States join(String[] args) throws ResponseException {
        if (args.length < 3) {
            System.out.println("Incorrect number of parameters\n");
            return UILoop.States.LOGGED_IN;
        }
        String mode = arg1;
        if (mode.equals("-p")) {
            if (args.length != 4) {
                System.out.println("Incorrect number of parameters\n");
                return UILoop.States.LOGGED_IN;
            }
            int gameID = Integer.parseInt(arg2);
            String color = arg3.toUpperCase();
            server.joinGame(color, gameID, this.auth.getAuthToken());
            System.out.println("Joined game " + gameID + " as " + color + "\n");
            return UILoop.States.PLAYING;
        } else if (mode.equals("-s")) {
            if (args.length != 3) {
                System.out.println("Incorrect number of parameters\n");
                return UILoop.States.LOGGED_IN;
            }
            int gameID = Integer.parseInt(arg2);
            System.out.println("Spectating game " + gameID + "\n");
            return UILoop.States.SPECTATING;
        } else {
            System.out.println("Unknown mode. Use -p for playing or -s for spectating\n");
            return UILoop.States.LOGGED_IN;
        }
    }

    public AuthData getAuth() {
        return auth;
    }

    public UILoop.States getState() {
        return state;
    }
}

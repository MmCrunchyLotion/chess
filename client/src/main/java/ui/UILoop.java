package ui;

import client.ServerFacade;
import static java.lang.Boolean.TRUE;

public class UILoop {

    public enum states {
        LOGGED_OUT,
        LOGGED_IN,
        PLAYING,
        SPECTATING
    }

    public static void UILoop() {
        states state = states.LOGGED_OUT;
        String response;
        ServerFacade facade = new ServerFacade("http://localhost:8080");
        LoggedOutHandler loggedOutHandler = new LoggedOutHandler(facade);
        LoggedInHandler loggedInHandler = new LoggedInHandler(facade);
        PlayingHandler playingHandler = new PlayingHandler(facade);
        SpectatingHandler spectatingHandler = new SpectatingHandler(facade);

        while (TRUE) {
            response = System.in.toString();
            ResponseParser parsed = new ResponseParser(response);
            String[] args = parsed.getTokens();
            if (args.length >= 5 || args.length == 0) {
                System.out.println("Invalid command\n");
            }
            if (args[0].equals("exit")) {
                if (state != states.LOGGED_OUT) {
                    loggedInHandler.logout();
                    if (state == states.PLAYING) {
                        playingHandler.quit();
                    } else if (state == states.SPECTATING) {
                        spectatingHandler.quit();
                    }
                }
                break;
            } else if (args[0].equals("help")) {
                help(state);
            } else if (state == states.LOGGED_OUT) {
                loggedOutHandler.handle(args);
            } else if (state == states.LOGGED_IN) {
                loggedInHandler.handle(args);
            } else if (state == states.PLAYING) {
                playingHandler.handle(args);
            } else if (state == states.SPECTATING) {
                spectatingHandler.handle(args);
            }
            System.out.printf("[%s] >>> ", state);
        }
        // close server
    }

    public static void help(states state) {
        if (state == states.LOGGED_IN) {
            System.out.println("help - shows possible commands\n");
            System.out.println("logout - logout current user\n");
            System.out.println("create <GAMENAME> - create a new chess game\n");
            System.out.println("list -  list existing chess games\n");
            System.out.println("join <GAMENAME> <COLOR> - join a chess game\n");
            System.out.println("exit - exit the client\n");
        }
        if (state == states.PLAYING) {
            System.out.println("help - shows possible commands\n");
            System.out.println("move <MOVE> - move a piece\n");
            System.out.println("quit - exit a chess game\n");
            System.out.println("exit - exit the client\n");
        }
        if (state == states.SPECTATING) {
            System.out.println("help - shows possible commands\n");
            System.out.println("quit - exit a chess game\n");
            System.out.println("exit - exit the client\n");
        }
    }
}

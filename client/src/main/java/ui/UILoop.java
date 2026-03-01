package ui;

import static java.lang.Boolean.TRUE;

public class UILoop {

    public enum states {
        LOGGED_OUT,
        LOGGED_IN,
        PLAYING,
        SPECTATING
    }

    public static void main() {
        states state = states.LOGGED_OUT;
        String response;
        LoggedOutHandler loggedOutHandler = new LoggedOutHandler();
        LoggedInHandler loggedInHandler = new LoggedInHandler();
        PlayingHandler playingHandler = new PlayingHandler();
        SpectatingHandler spectatingHandler = new SpectatingHandler();


        while (TRUE) {
            response = System.in.toString();
            ResponseParser parsed = new ResponseParser(response);
            String[] args = parsed.getTokens();
            if (args.length >= 5 || args.length == 0) {
                System.out.println("Invalid command\n");
            }
            if (args[0].equals("exit")) {
                break;
            } else if (args[0].equals("help")) {
                help(state);
            } else if (state == states.LOGGED_OUT) {

            } else if (state == states.LOGGED_IN) {

            } else if (state == states.PLAYING) {

            } else if (state == states.SPECTATING) {

            }
            System.out.printf("[%s] >>> ", state);
        }
    }
    
    
    public static void help(states state) {
        if (state == states.LOGGED_OUT) {
            System.out.println("help - shows possible commands\n");
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - create an account\n");
            System.out.println("login <USERNAME> <PASSWORD> - login to an existing account\n");
            System.out.println("exit - exit the client\n");
        }
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

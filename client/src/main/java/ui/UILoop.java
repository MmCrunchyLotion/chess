package ui;

import client.ServerFacade;
import exception.ResponseException;
import models.AuthData;
import java.util.Scanner;

public class UILoop {

    private AuthData auth;
    private States state;
    private final Scanner scanner = new Scanner(System.in);

    public enum States {
        LOGGED_OUT,
        LOGGED_IN,
        PLAYING,
        SPECTATING
    }

    public UILoop() {
        this.auth = null;
        this.state = States.LOGGED_OUT;
    }

    public void startUILoop() throws ResponseException {
        ServerFacade facade = new ServerFacade("http://localhost:8080");
        LoggedOutHandler loggedOutHandler = new LoggedOutHandler(facade);
        // playing and spectating handlers start as null, created when joining
        PlayingHandler[] playingHandler = {null};
        SpectatingHandler[] spectatingHandler = {null};
        LoggedInHandler loggedInHandler = new LoggedInHandler(facade, playingHandler, spectatingHandler);

        boolean running = true;
        while (running) {
            System.out.printf("[%s] >>> ", state);
            String response = scanner.nextLine().trim();
            if (response.isEmpty()) continue;

            ResponseParser parsed = new ResponseParser(response);
            String[] args = parsed.getTokens();

            if (args.length > 4) {
                System.out.println("Too many arguments. Type 'help' for a list of commands.\n");
                continue;
            }
            if (args.length == 0) {
                System.out.println("Invalid command\n");
                continue;
            }

            if (args[0].equalsIgnoreCase("quit")) {
                if (args.length != 1) {
                    System.out.println("Usage: quit\n");
                    continue;
                }
                switch (this.state) {
                    case PLAYING, SPECTATING -> {
                        try {
                            if (this.auth != null) {
                                loggedInHandler.logout(new String[]{"logout"});
                            }
                        } catch (ResponseException e) {
                            System.out.println("Error logging out: " + e.getMessage() + "\n");
                        }
                        running = false;
                    }
                    case LOGGED_IN -> {
                        this.state = loggedInHandler.logout(args);
                        running = false;
                    }
                    case LOGGED_OUT -> running = false;
                }
                continue;
            }

            try {
                switch (state) {
                    case LOGGED_OUT -> {
                        loggedOutHandler.handle(args);
                        this.auth = loggedOutHandler.getAuth();
                        if (this.auth != null) {
                            this.state = States.LOGGED_IN;
                            loggedInHandler.setState(States.LOGGED_IN);
                        }
                    }
                    case LOGGED_IN -> {
                        loggedInHandler.handle(args, auth);
                        this.auth = loggedInHandler.getAuth();
                        this.state = loggedInHandler.getState();
                    }
                    case PLAYING -> {
                        if (playingHandler[0] != null) {
                            playingHandler[0].handle(args);
                            this.state = playingHandler[0].getState();
                        }
                    }
                    case SPECTATING -> {
                        if (spectatingHandler[0] != null) {
                            spectatingHandler[0].handle(args);
                            this.state = spectatingHandler[0].getState();
                        }
                    }
                }
            } catch (ResponseException e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }
}

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
        LoggedInHandler loggedInHandler = new LoggedInHandler(facade);
        PlayingHandler playingHandler = new PlayingHandler(facade);
        SpectatingHandler spectatingHandler = new SpectatingHandler(facade);

        boolean running = true;
        while (running) {
            System.out.printf("[%s] >>> ", state);
            String response = scanner.nextLine().trim();
            if (response.isEmpty()) continue;

            ResponseParser parsed = new ResponseParser(response);
            String[] args = parsed.getTokens();

            if (args.length > 4) {
                System.out.println("Too many arguments. Type 'help' for a list of commands.\n");
            }
            if (args.length == 0) {
                System.out.println("Invalid command\n");
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
                        UILoop.States newState = loggedInHandler.getState();
                        if (newState == States.PLAYING || newState == States.SPECTATING) {
                            playingHandler.setState(newState);
                            spectatingHandler.setState(newState);
                        }
                        this.state = newState;
                    }
                    case PLAYING -> {
                        playingHandler.handle(args);
                        this.state = playingHandler.getState();
                    }
                    case SPECTATING -> {
                        spectatingHandler.handle(args);
                        this.state = spectatingHandler.getState();
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

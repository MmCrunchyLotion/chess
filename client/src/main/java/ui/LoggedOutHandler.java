package ui;

import client.ServerFacade;
import exception.ResponseException;
import models.AuthData;
import models.UserData;

public class LoggedOutHandler extends Handler {

    private final ServerFacade server;
    private AuthData auth;

    public LoggedOutHandler(ServerFacade facade) {
        this.server = facade;
        this.auth = null;
    }

    public void handle(String[] args) throws ResponseException {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "register" -> register(args);
            case "login" -> login(args);
            default -> System.out.println("Unknown command. Type 'help' for a list of commands.\n");
        }
        clearArgs();
    }

    private void help() {
        System.out.println("Available commands:");
        System.out.println("  help                              - shows possible commands");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - create an account");
        System.out.println("  login <USERNAME> <PASSWORD>       - login to an existing account");
        System.out.println("  exit                              - exit the client\n");
    }

    private void register(String[] args) throws ResponseException {
        if (args.length != 4) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>\n");
        } else {
            this.auth = server.register(new UserData(arg1, arg2, arg3));
            System.out.println("Registered and logged in as " + arg1 + "\n");
        }
    }

    private void login(String[] args) throws ResponseException {
        if (args.length != 3) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>\n");
        } else {
            this.auth = server.login(new UserData(arg1, arg2, null));
            System.out.println("Logged in as " + arg1 + "\n");
        }
    }

    public AuthData getAuth() {
        return auth;
    }
}
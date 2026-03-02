package ui;

import client.ServerFacade;
import exception.ResponseException;
import models.UserData;

public class LoggedOutHandler extends Handler {

    private ServerFacade server;
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;

    public LoggedOutHandler(ServerFacade facade) {
        this.server = facade;
    }

    public void handle(String[] args) throws ResponseException {
        setArgs(args);
        switch (arg0) {
            case "help" -> help();
            case "register" -> register(args);
            case "login" -> login(args);
        }
        clearArgs();
    }

    private void help() {
        System.out.println("help - shows possible commands\n");
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - create an account\n");
        System.out.println("login <USERNAME> <PASSWORD> - login to an existing account\n");
        System.out.println("exit - exit the client\n");
    }

    private void register(String[] args) throws ResponseException {
        if (args.length != 4) {
            System.out.println("Incorrect number of parameters\n");
        } else {
            server.register(new UserData(arg1, arg2, arg3));
        }
    }

    private void login(String[] args) throws ResponseException {
        if (args.length != 3) {
            System.out.println("Incorrect number of parameters\n");
        } else {
            server.login(new UserData(arg1, arg2, null));
        }
    }
}

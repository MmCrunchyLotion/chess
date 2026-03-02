package ui;

import client.ServerFacade;

public class SpectatingHandler extends Handler {

    private final ServerFacade server;
    private UILoop.States state;

    public SpectatingHandler(ServerFacade facade) {
        this.server = facade;
        this.state = UILoop.States.SPECTATING;
    }

    public void handle(String[] args) {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "quit" -> this.state = quit();
            default -> System.out.println("Unknown command. Type 'help' for a list of commands.\n");
        }
        clearArgs();
    }

    private static void help() {
        System.out.println("Available commands:");
        System.out.println("  help  - shows possible commands");
        System.out.println("  quit  - stop spectating");
        System.out.println("  exit  - exit the client\n");
    }

    public UILoop.States quit() {
        System.out.println("Stopped spectating\n");
        return UILoop.States.LOGGED_IN;
    }

    public UILoop.States getState() {
        return state;
    }

    public void setState(UILoop.States state) {
        this.state = state;
    }
}
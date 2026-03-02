package ui;

import client.ServerFacade;

public class PlayingHandler extends Handler {

    private final ServerFacade server;
    private UILoop.States state;

    public PlayingHandler(ServerFacade facade) {
        this.server = facade;
        this.state = UILoop.States.PLAYING;
    }

    public void handle(String[] args) {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "move" -> move(args);
            case "quit" -> this.state = quit();
            default -> System.out.println("Unknown command. Type 'help' for a list of commands.\n");
        }
        clearArgs();
    }

    private static void help() {
        System.out.println("Available commands:");
        System.out.println("  help          - shows possible commands");
        System.out.println("  move <FROM> <TO> - move a piece (e.g. move e2 e4)");
        System.out.println("  quit          - exit the current game");
        System.out.println("  exit          - exit the client\n");
    }

    private void move(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)\n");
        } else {
            // TODO: implement move logic when websocket is added
            System.out.println("Moving from " + arg1 + " to " + arg2 + "\n");
        }
    }

    public UILoop.States quit() {
        System.out.println("Left the game\n");
        return UILoop.States.LOGGED_IN;
    }

    public UILoop.States getState() {
        return state;
    }

    public void setState(UILoop.States state) {
        this.state = state;
    }
}
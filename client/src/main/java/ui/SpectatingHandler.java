package ui;

import chess.ChessGame;
import chess.ChessPosition;
import client.WebSocketFacade;
import exception.ResponseException;
import models.AuthData;

public class SpectatingHandler extends Handler implements WebSocketFacade.MessageHandler {

    private final WebSocketFacade ws;
    private UILoop.States state;
    private ChessGame currentGame;
    private final int gameID;
    private final AuthData auth;

    public SpectatingHandler(WebSocketFacade ws, AuthData auth, int gameID) {
        this.ws = ws;
        this.auth = auth;
        this.gameID = gameID;
        this.state = UILoop.States.SPECTATING;
    }

    @Override
    public void onLoadGame(ChessGame game) {
        this.currentGame = game;
        BoardDisplay.drawBoard(game, ChessGame.TeamColor.WHITE);
        System.out.printf("%n[SPECTATING] >>> ");
    }

    @Override
    public void onNotification(String message) {
        System.out.println("\n" + message);
        System.out.printf("[SPECTATING] >>> ");
    }

    @Override
    public void onError(String errorMessage) {
        System.out.println("\nError: " + errorMessage);
        System.out.printf("[SPECTATING] >>> ");
    }

    public void handle(String[] args) throws ResponseException {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "redraw" -> redraw();
            case "highlight" -> highlight(args);
            case "leave" -> this.state = leave();
            default -> System.out.println("Unknown command. Type 'help' for a list of commands.\n");
        }
        clearArgs();
    }

    private void help() {
        System.out.println("Available commands:");
        System.out.println("  help                    - shows possible commands");
        System.out.println("  redraw                  - redraws the chess board");
        System.out.println("  highlight <SQUARE>      - highlight legal moves for a piece (e.g. highlight e2)");
        System.out.println("  leave                   - stop spectating");
        System.out.println("  quit                    - exit the client\n");
    }

    private void redraw() {
        if (currentGame != null) {
            BoardDisplay.drawBoard(currentGame, ChessGame.TeamColor.WHITE);
        } else {
            System.out.println("No game to display\n");
        }
    }

    private void highlight(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: highlight <SQUARE> (e.g. highlight e2)\n");
            return;
        }
        if (currentGame == null) {
            System.out.println("No game to display\n");
            return;
        }
        try {
            ChessPosition pos = parsePosition(arg1);
            var legalMoves = currentGame.validMoves(pos);
            BoardDisplay.drawBoardWithHighlights(currentGame, ChessGame.TeamColor.WHITE, pos, legalMoves);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format. Use letter+number (e.g. e2)\n");
        }
    }

    public UILoop.States leave() throws ResponseException {
        ws.leave(auth.getAuthToken(), gameID);
        ws.close();
        this.state = UILoop.States.LOGGED_IN;
        System.out.println("Stopped spectating\n");
        return UILoop.States.LOGGED_IN;
    }

    private ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) throw new IllegalArgumentException("Invalid position");
        int col = pos.charAt(0) - 'a' + 1;
        int row = pos.charAt(1) - '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) throw new IllegalArgumentException("Out of bounds");
        return new ChessPosition(row, col);
    }

    public UILoop.States getState() { return state; }
    public void setState(UILoop.States state) { this.state = state; }
}
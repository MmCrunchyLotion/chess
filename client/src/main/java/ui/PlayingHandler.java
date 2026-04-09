package ui;

import chess.*;
import client.WebSocketFacade;
import exception.ResponseException;
import models.AuthData;

public class PlayingHandler extends Handler implements WebSocketFacade.MessageHandler {

    private final WebSocketFacade ws;
    private UILoop.States state;
    private ChessGame currentGame;
    private ChessGame.TeamColor playerColor;
    private final int gameID;
    private final AuthData auth;
    private boolean leaving = false;

    public PlayingHandler(WebSocketFacade ws, AuthData auth, int gameID, ChessGame.TeamColor color) {
        this.ws = ws;
        this.auth = auth;
        this.gameID = gameID;
        this.playerColor = color;
        this.state = UILoop.States.PLAYING;
    }

    @Override
    public void onLoadGame(ChessGame game) {
        if (leaving) return;
        this.currentGame = game;
        printGameStatus(game);
        BoardDisplay.drawBoard(game, playerColor);
        System.out.printf("%n[PLAYING] >>> ");
    }

    @Override
    public void onNotification(String message) {
        if (leaving) return;
        System.out.println("\n" + message);
        System.out.printf("[PLAYING] >>> ");
    }

    @Override
    public void onError(String errorMessage) {
        if (leaving) return;
        System.out.println("\n" + errorMessage);
        System.out.printf("[PLAYING] >>> ");
    }

    public void handle(String[] args) throws ResponseException {
        setArgs(args);
        switch (arg0.toLowerCase()) {
            case "help" -> help();
            case "redraw" -> redraw();
            case "move" -> move(args);
            case "resign" -> resign();
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
        System.out.println("  move <FROM> <TO>        - move a piece (e.g. move e2 e4)");
        System.out.println("  highlight <SQUARE>      - highlight legal moves for a piece (e.g. highlight e2)");
        System.out.println("  resign                  - forfeit the game");
        System.out.println("  leave                   - leave the game");
        System.out.println("  quit                    - exit the client\n");
    }

    private void printGameStatus(ChessGame game) {
        if (game.isGameOver()) {
            System.out.println("\n*** Game Over ***");
        } else {
            ChessGame.TeamColor turn = game.getTeamTurn();
            String turnName = turn == ChessGame.TeamColor.WHITE ? "White" : "Black";
            if (turn == playerColor) {
                System.out.println("\nYour turn (" + turnName + ")");
            } else {
                System.out.println("\n" + turnName + "'s turn");
            }
        }
    }

    private void redraw() {
        if (currentGame != null) {
            printGameStatus(currentGame);
            BoardDisplay.drawBoard(currentGame, playerColor);
        } else {
            System.out.println("No game to display\n");
        }
    }

    private void move(String[] args) throws ResponseException {
        if (args.length < 3) {
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)\n");
            return;
        }
        try {
            ChessPosition from = parsePosition(arg1);
            ChessPosition to = parsePosition(arg2);
            ChessPiece.PieceType promotion = null;
            if (args.length == 4) {
                promotion = parsePromotion(arg3);
                if (promotion == null) {
                    System.out.println("Invalid promotion piece. Use: queen, rook, bishop, knight\n");
                    return;
                }
            }
            ws.makeMove(auth.getAuthToken(), gameID, new ChessMove(from, to, promotion));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format. Use letter+number (e.g. e2)\n");
        }
    }

    private void resign() throws ResponseException {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("yes")) {
            ws.resign(auth.getAuthToken(), gameID);
            if (currentGame != null) {
                currentGame.setGameOver(true);
                printGameStatus(currentGame);
                BoardDisplay.drawBoard(currentGame, playerColor);
            }
        } else {
            System.out.println("Resign cancelled\n");
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
            BoardDisplay.drawBoardWithHighlights(currentGame, playerColor, pos, legalMoves);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format. Use letter+number (e.g. e2)\n");
        }
    }

    public UILoop.States leave() throws ResponseException {
        leaving = true;
        ws.leave(auth.getAuthToken(), gameID);
        ws.close();
        this.state = UILoop.States.LOGGED_IN;
        System.out.println("Left the game\n");
        return UILoop.States.LOGGED_IN;
    }

    private ChessPosition parsePosition(String pos) {
        return getChessPosition(pos);
    }

    static ChessPosition getChessPosition(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position");
        }
        int col = pos.charAt(0) - 'a' + 1;
        int row = pos.charAt(1) - '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Out of bounds");
        }
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String piece) {
        return switch (piece.toLowerCase()) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    public UILoop.States getState() { return state; }
    public void setState(UILoop.States state) { this.state = state; }
}
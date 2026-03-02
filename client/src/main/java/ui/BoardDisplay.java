package ui;

import chess.*;

public class BoardDisplay {

    // border
    private static final String BORDER_BG = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String BORDER_TEXT = EscapeSequences.SET_TEXT_COLOR_WHITE;

    // squares
    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_BLACK;

    // pieces
    private static final String WHITE_PIECE = EscapeSequences.SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE = EscapeSequences.SET_TEXT_COLOR_BLUE;

    private static final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

    public static void drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        boolean isWhite = perspective == ChessGame.TeamColor.WHITE || perspective == null;
        System.out.println();
        if (isWhite) {
            drawFromWhitePerspective(game.getBoard());
        } else {
            drawFromBlackPerspective(game.getBoard());
        }
        System.out.println();
    }

    private static void drawFromWhitePerspective(ChessBoard board) {
        printColumnLabels(false);
        for (int row = 8; row >= 1; row--) {
            printRow(board, row, false);
        }
        printColumnLabels(false);
    }

    private static void drawFromBlackPerspective(ChessBoard board) {
        printColumnLabels(true);
        for (int row = 1; row <= 8; row++) {
            printRow(board, row, true);
        }
        printColumnLabels(true);
    }

    private static void printRow(ChessBoard board, int row, boolean reversed) {
        System.out.print(BORDER_BG + BORDER_TEXT + " " + row + "\u2003");  // em-space after row number
        if (reversed) {
            for (int col = 8; col >= 1; col--) {
                printSquare(board, row, col);
            }
        } else {
            for (int col = 1; col <= 8; col++) {
                printSquare(board, row, col);
            }
        }
        System.out.print(BORDER_BG + BORDER_TEXT + " " + row + "\u2003");
        System.out.println(RESET);
    }

    private static void printColumnLabels(boolean reversed) {
        System.out.print(BORDER_BG + BORDER_TEXT + "  \u2003");
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        if (reversed) {
            for (int i = 7; i >= 0; i--) {
                System.out.print(" " + cols[i] + "\u2003");
            }
        } else {
            for (String col : cols) {
                System.out.print(" " + col + "\u2003");
            }
        }
        System.out.println("  \u2003" + RESET);
    }

    private static void printSquare(ChessBoard board, int row, int col) {
        boolean isLight = (row + col) % 2 != 0;
        String bgColor = isLight ? LIGHT_SQUARE : DARK_SQUARE;
        System.out.print(bgColor);

        ChessPosition pos = board.getGridPosition(row, col);
        ChessPiece piece = pos.getOccupied();

        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
        } else {
            String pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PIECE : BLACK_PIECE;
            System.out.print(pieceColor + getPieceSymbol(piece) + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}

import chess.*;

public class Main {

    public enum states {
        LOGGED_OUT,
        LOGGED_IN,
        PLAYING,
        SPECTATING
    }

    public static void main(String[] args) {

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece + "\n");

        System.out.println("♕ Welcome to 240 chess. Type 'Help' to get started. ♕ \n");
    }
}
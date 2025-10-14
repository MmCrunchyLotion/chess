package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that returns a collection of moves that a piece can make.
 * <p>
 * Note: Does not take into account check or checkmate.
 * </p>
 */
public class MoveCalculator {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final ChessPiece piece;
    private Collection<ChessMove> moves;

    public MoveCalculator(ChessBoard board, ChessPosition position, ChessPiece piece) {
        this.board = board;
        this.myPosition = board.getPosition(position);
        this.piece = piece;
        this.moves = new ArrayList<>();
    }

    public Collection<ChessMove> getKingMoves() {
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
        moveByDirection(directions);
        if (!piece.isMoved() && myPosition.getColumn() == 5) {
            int row = myPosition.getRow();
            if (canCastleQueenside(row)) {
                moves.add(new ChessMove(myPosition, board.getGridPosition(row, 3), null));
            }
            if (canCastleKingside(row)) {
                moves.add(new ChessMove(myPosition, board.getGridPosition(row, 7), null));
            }
        }
        return moves;
    }

    public Collection<ChessMove> getQueenMoves() {
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
        slideByDirection(directions);
        return moves;
    }

    public Collection<ChessMove> getBishopMoves() {
        int[][] directions = {{-1,-1},{-1,1},{1,-1},{1,1}};
        slideByDirection(directions);
        return moves;
    }
    public Collection<ChessMove> getKnightMoves() {
        int[][] directions = {{1,-2},{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}};
        moveByDirection(directions);
        return moves;
    }

    public Collection<ChessMove> getRookMoves() {
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        slideByDirection(directions);
        return moves;
    }

    public Collection<ChessMove> getPawnMoves() {
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 7 : 2;
        int enPassantRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 5 : 4;
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        ChessPosition target = board.getGridPosition(row + direction, column);
        if (target.getOccupied() == null) {
            if (row == startRow) {
                ChessPosition secondTarget = board.getGridPosition(row + (2 * direction), column);
                if (secondTarget.getOccupied() == null) {
                    moves.add(new ChessMove(myPosition, secondTarget, null));
                }
                moves.add(new ChessMove(myPosition, target, null));

            } else if (row == promotionRow) {
                addPromotions(target);
            } else {
                moves.add(new ChessMove(myPosition, target, null));
            }
        }

        if (column > 1) {
            ChessPosition leftAtk = board.getGridPosition(row + direction, column -1);
            attack(row, promotionRow, leftAtk);
        }
        if (column < 8) {
            ChessPosition rightAtk = board.getGridPosition(row + direction, column + 1);
            attack(row, promotionRow, rightAtk);
        }

        // en passant
        if (row == enPassantRow && board.getLastMove() != null) {
            ChessMove lastMove = board.getLastMove();
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (lastMovedPiece != null && lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN && lastMovedPiece.getTeamColor() != piece.getTeamColor()) {
                int rowDifference = Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow());
                if (rowDifference == 2) {
                    int attackColumn = lastMove.getEndPosition().getColumn();
                    if (Math.abs(attackColumn - column) == 1 && lastMove.getEndPosition().getRow() == row) {
                        ChessPosition captureSquare = board.getGridPosition(row + direction, attackColumn);
                        moves.add(new ChessMove(myPosition, captureSquare, null));
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Attack helper for Pawn
     */
     private void attack(int row, int promotionRow, ChessPosition target) {
        if (target.getOccupied() != null) {
            if (target.getOccupied().getTeamColor() != piece.getTeamColor()) {
                if (row == promotionRow) {
                    addPromotions(target);
                } else {
                    moves.add(new ChessMove(myPosition, target, null));
                }
            }
        }
    }

    /**
     * Pawn Promotion helper
     */
    private void addPromotions(ChessPosition target) {
        moves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.ROOK));
    }

    /**
     * King and knight move helper
     */
    private void moveByDirection(int[][] directions) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] direction : directions) {
            if (row + direction[0] < 9 && row + direction[0] > 0 && column + direction[1] < 9 && column + direction[1] > 0){
                ChessPosition target = board.getGridPosition(row + direction[0], column + direction[1]);
                if (target.getOccupied() != null) {
                    if (target.getOccupied().getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, target, null));
                }
            }
        }
    }

    /**
     * Queen, Bishop and Rook helper
     */
    private void slideByDirection(int[][] directions) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] direction : directions) {
            int rowMod = direction[0];
            int colMod = direction[1];
            while (row + direction[0] < 9 && row + direction[0] > 0 && column + direction[1] < 9 && column + direction[1] > 0){
                ChessPosition target = board.getGridPosition(row + direction[0], column + direction[1]);
                if (target.getOccupied() != null) {
                    if (target.getOccupied().getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, target, null));
                }
                direction[0] += rowMod;
                direction[1] += colMod;
            }
        }
    }

    /**
     * Checks if pieces are blocking kingside castle
     */
    private boolean canCastleKingside(int row) {
        ChessPosition rookPosition = board.getGridPosition(row, 8);
        if (rookPosition.getOccupied() == null || rookPosition.getOccupied().isMoved()) {
            return false;
        }
        for (int column = 6; column <= 7; column++) {
            if (board.getGridPosition(row, column).getOccupied() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if pieces are blocking queenside castle
     */
    private boolean canCastleQueenside(int row) {
        ChessPosition rookPosition = board.getGridPosition(row, 1);
        if (rookPosition.getOccupied() == null || rookPosition.getOccupied().isMoved()) {
            return false;
        }
        for (int column = 2; column <= 4; column++) {
            if (board.getGridPosition(row, column).getOccupied() != null) {
                return false;
            }
        }
        return true;
    }
}

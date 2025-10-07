package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculator {

    private ChessBoard board;
    private ChessPosition myPosition;
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessGame game;

    public MoveCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        setBoard(board);
        setMyPosition(myPosition);
        setPiece(piece);
        setMoves(new ArrayList<>());
        this.game = game;
    }

    public Collection<ChessMove> getKingMoves() {
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
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
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        slideByDirection(directions);
        return moves;
    }

    public Collection<ChessMove> getBishopMoves() {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        slideByDirection(directions);
        return moves;
    }
    public Collection<ChessMove> getKnightMoves() {
        int[][] directions = {{1,-2 }, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}};
        moveByDirection(directions);
        return moves;
    }

    public Collection<ChessMove> getRookMoves() {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        slideByDirection(directions);
        return moves;
    }
    public Collection<ChessMove> getPawnMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (getPiece().getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (getPiece().getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (getPiece().getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int enPassantRow = (getPiece().getTeamColor() == ChessGame.TeamColor.WHITE) ? 5 : 4;
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        // double move
        if (row == startRow){
            if (board.getGridPosition(row + (2 * direction), column).getOccupied() == null && board.getGridPosition(row + direction, column).getOccupied() == null){
                moves.add(new ChessMove(myPosition, board.getGridPosition(row + (2 * direction), column), null));
            }
        }

        // single move
        if (board.getGridPosition(row + direction, column).getOccupied() == null){
            if (row + direction == promotionRow) {
                addPromotions(myPosition, board.getGridPosition(row + direction, column), moves);
            } else {
                moves.add(new ChessMove(myPosition, board.getGridPosition(row + direction, column), null));
            }
        }

        // capture left
        if (column > 1) {
            ChessPiece leftTarget = board.getGridPosition(row + direction, column - 1).getOccupied();
            if (leftTarget != null && leftTarget.getTeamColor() != getPiece().getTeamColor()) {
                if (row + direction == promotionRow) {
                    addPromotions(myPosition, board.getGridPosition(row + direction, column - 1), moves);
                } else {
                    moves.add(new ChessMove(myPosition, board.getGridPosition(row + direction, column - 1), null));
                }
            }
        }

        // capture right
        if (column < 8) {
            ChessPiece rightTarget = board.getGridPosition(row + direction, column + 1).getOccupied();
            if (rightTarget != null && rightTarget.getTeamColor() != getPiece().getTeamColor()) {
                if (row + direction == promotionRow) {
                    addPromotions(myPosition, board.getGridPosition(row + direction, column + 1), moves);
                } else {
                    moves.add(new ChessMove(myPosition, board.getGridPosition(row + direction, column + 1), null));
                }
            }
        }

        // en passant
        if (row == enPassantRow && game != null && game.getLastMove() != null) {
            ChessMove lastMove = game.getLastMove();
            // Check if last move was a pawn double-move
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (lastMovedPiece != null
                    && lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN
                    && lastMovedPiece.getTeamColor() != getPiece().getTeamColor()) {
                int rowDiff = Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow());
                if (rowDiff == 2) {
                    int enemyCol = lastMove.getEndPosition().getColumn();
                    if (Math.abs(enemyCol - column) == 1 && lastMove.getEndPosition().getRow() == row) {
                        ChessPosition captureSquare = board.getGridPosition(row + direction, enemyCol);
                        moves.add(new ChessMove(myPosition, captureSquare, null));
                    }
                }
            }
        }

        return moves;
    }

    private void moveByDirection(int[][] directions) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                if (board.getGridPosition(newRow, newColumn).getOccupied() == null) {
                    moves.add(new ChessMove(myPosition, board.getGridPosition(newRow, newColumn), null));
                } else {
                    if (board.getGridPosition(newRow, newColumn).getOccupied().getTeamColor() != getPiece().getTeamColor()) {
                        moves.add(new ChessMove(myPosition, board.getGridPosition(newRow, newColumn), null));
                    }
                }
            }
        }
    }

    private void slideByDirection(int[][] directions) {
        int startRow = myPosition.getRow();
        int startColumn = myPosition.getColumn();

        for (int[] direction : directions) {
            int newRow = startRow + direction[0];
            int newColumn = startColumn + direction[1];
            while (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8){
                if (board.getGridPosition(newRow, newColumn).getOccupied() == null) {
                    moves.add(new ChessMove(myPosition, board.getGridPosition(newRow, newColumn), null));
                } else {
                    if (board.getGridPosition(newRow, newColumn).getOccupied().getTeamColor() != getPiece().getTeamColor()) {
                        moves.add(new ChessMove(myPosition, board.getGridPosition(newRow, newColumn), null));
                    }
                    break;
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
    }

    private boolean canCastleQueenside(int row) {
        ChessPosition rookPosition = board.getGridPosition(row, 1);
        if (rookPosition.getOccupied() == null || rookPosition.getOccupied().isMoved()) {
            return false;
        }
        for (int col = 2; col <= 4; col++) {
            if (board.getGridPosition(row, col).getOccupied() != null) {
                return false;
            }
        }
        return true;
    }

    private boolean canCastleKingside(int row) {
        ChessPosition rookPosition = board.getGridPosition(row, 8);
        if (rookPosition.getOccupied() == null || rookPosition.getOccupied().isMoved()) {
            return false;
        }
        for (int col = 6; col <= 7; col++) {
            if (board.getGridPosition(row, col).getOccupied() != null) {
                return false;
            }
        }
        return true;
    }

    private void addPromotions(ChessPosition from, ChessPosition to, Collection<ChessMove> moves) {
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void setMyPosition(ChessPosition myPosition) {
        this.myPosition = myPosition;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public void setMoves(Collection<ChessMove> moves) {
        this.moves = moves;
    }
}

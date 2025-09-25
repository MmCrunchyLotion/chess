package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor team;
    private PieceType piece;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        setTeam(pieceColor);
        setPiece(type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (piece) {
            case KING -> getKingMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case BISHOP-> getBishopMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case PAWN -> getPawnMoves(board, myPosition);
        };
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        moveByDirection(board, myPosition, directions, moves);
        return moves;
    }
    
    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        slideByDirection(board, myPosition, directions, moves);
        return moves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        slideByDirection(board, myPosition, directions, moves);
        return moves;
    }
    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1,-2 }, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}};
        moveByDirection(board, myPosition, directions, moves);
        return moves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        slideByDirection(board, myPosition, directions, moves);
        return moves;
    }
    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (this.team == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (this.team == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (this.team == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        // double move
        if (row == startRow){
            if (board.getPosition(row + (2 * direction), column).getOccupied() == null && board.getPosition(row + direction, column).getOccupied() == null){
                moves.add(new ChessMove(myPosition, board.getPosition(row + (2 * direction), column), null));
            }
        }

        // single move
        if (board.getPosition(row + direction, column).getOccupied() == null){
            if (row + direction == promotionRow) {
                addPromotions(myPosition, board.getPosition(row + direction, column), moves);
            } else {
                moves.add(new ChessMove(myPosition, board.getPosition(row + direction, column), null));
            }
        }

        // capture left
        if (column > 1 && board.getPosition(row + direction, column - 1).getOccupied() != null) {
            if (board.getPosition(row + direction, column - 1).getOccupied().getTeamColor() != this.team){
                if (row + direction == promotionRow) {
                    addPromotions(myPosition, board.getPosition(row + direction, column - 1), moves);
                } else {
                    moves.add(new ChessMove(myPosition, board.getPosition(row + direction, column - 1), null));
                }
            }
        }

        // capture right
        if (column < 8 && board.getPosition(row + direction, column + 1).getOccupied() != null){
            if (board.getPosition(row + direction, column + 1).getOccupied().getTeamColor() != this.team){
                if (row + direction == promotionRow) {
                    addPromotions(myPosition, board.getPosition(row + direction, column + 1), moves);
                } else {
                    moves.add(new ChessMove(myPosition, board.getPosition(row + direction, column + 1), null));
                }
            }
        }
        
        return moves;
    }

    private void moveByDirection(ChessBoard board, ChessPosition myPosition, int[][] directions, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                if (board.getPosition(newRow, newColumn).getOccupied() == null) {
                    moves.add(new ChessMove(myPosition, board.getPosition(newRow, newColumn), null));
                } else {
                    if (board.getPosition(newRow, newColumn).getOccupied().getTeamColor() != team) {
                        moves.add(new ChessMove(myPosition, board.getPosition(newRow, newColumn), null));
                    }
                }
            }
        }
    }
    
    private void slideByDirection(ChessBoard board, ChessPosition myPosition, int[][] directions, Collection<ChessMove> moves) {
        int startRow = myPosition.getRow();
        int startColumn = myPosition.getColumn();

        for (int[] direction : directions) {
            int newRow = startRow + direction[0];
            int newColumn = startColumn + direction[1];
            while (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8){
                if (board.getPosition(newRow, newColumn).getOccupied() == null) {
                    moves.add(new ChessMove(myPosition, board.getPosition(newRow, newColumn), null));
                } else {
                    if (board.getPosition(newRow, newColumn).getOccupied().getTeamColor() != team) {
                        moves.add(new ChessMove(myPosition, board.getPosition(newRow, newColumn), null));
                    }
                    break;
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
    }

    private void addPromotions(ChessPosition from, ChessPosition to, Collection<ChessMove> moves) {
        moves.add(new ChessMove(from, to, PieceType.QUEEN));
        moves.add(new ChessMove(from, to, PieceType.BISHOP));
        moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        moves.add(new ChessMove(from, to, PieceType.ROOK));
    }

    public void setTeam(ChessGame.TeamColor team) {
        this.team = team;
    }

    public void setPiece(PieceType piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return team == that.team && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, piece);
    }
}

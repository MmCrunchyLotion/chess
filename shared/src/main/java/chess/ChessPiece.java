package chess;

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
            case KING -> new MoveCalculator(board,myPosition,this).getKingMoves();
            case QUEEN -> new MoveCalculator(board,myPosition,this).getQueenMoves();
            case BISHOP-> new MoveCalculator(board,myPosition,this).getBishopMoves();
            case KNIGHT -> new MoveCalculator(board,myPosition,this).getKnightMoves();
            case ROOK -> new MoveCalculator(board,myPosition,this).getRookMoves();
            case PAWN -> new MoveCalculator(board,myPosition,this).getPawnMoves();
        };
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



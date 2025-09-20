package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition startPosition;
    private ChessPiece.PieceType pieceType;
    private ChessGame.TeamColor team;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,ChessPiece.PieceType promotionPiece) {

    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        throw new RuntimeException("Not implemented");
    }

    public void setStartPosition(ChessPosition startPosition) {
        this.startPosition = startPosition;
    }

    public ChessPiece.PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(ChessPiece.PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public ChessGame.TeamColor getTeam() {
        return team;
    }

    public void setTeam(ChessGame.TeamColor team) {
        this.team = team;
    }
}

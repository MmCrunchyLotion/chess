package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ChessGame.TeamColor TeamTurn;

    public ChessGame() {
        setBoard(new ChessBoard());
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.TeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.TeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPosition actualStartPosition = board.getPosition(startPosition.getRow(), startPosition.getColumn());
        if (actualStartPosition.getOccupied() != null) {
            return actualStartPosition.getOccupied().pieceMoves(board, actualStartPosition);
        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition givenStartPosition = move.getStartPosition();
        ChessPosition givenEndPosition = move.getEndPosition();
        ChessPosition startPosition = board.getPosition(givenStartPosition.getRow(), givenStartPosition.getColumn());
        ChessPosition endPosition = board.getPosition(givenEndPosition.getRow(), givenEndPosition.getColumn());
        if (validMoves(startPosition) != null) {
            if (validMoves(startPosition).contains(move)) {
                ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
                if (promotionPiece != null) {
                    board.addPiece(endPosition, new ChessPiece(getTeamTurn(), promotionPiece));
                    startPosition.setOccupied(null);
                } else {
                    board.addPiece(endPosition, startPosition.getOccupied());
                }
                TeamColor otherTeam = getOtherTeam();
                setTeamTurn(otherTeam);
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) /*throws InvalidMoveException*/ {
        ChessPosition kingPosition = findKing(teamColor);
        TeamColor otherTeam = getOtherTeam();
        for (ChessPosition position : findTeamPositions(otherTeam)) {
            for (ChessMove move : validMoves(position)) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) /*throws InvalidMoveException*/ {
        if (isInCheck(teamColor)) {
            return validMoves(findKing(teamColor)) == null;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) /*throws InvalidMoveException*/ {
        if (isInCheck(teamColor)) {
            return validMoves(findKing(teamColor)) == null;
        }
        return false;
    }

    private Collection<ChessPosition> findTeamPositions(TeamColor teamColor) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentSquare = board.getPosition(i, j);
                if (currentSquare.getOccupied() != null && currentSquare.getOccupied().getTeamColor() == teamColor) {
                    positions.add(currentSquare);
                }
            }
        }
        return positions;
    }

    private ChessPosition findKing(TeamColor teamColor) /*throws InvalidMoveException*/ {
        if (!findTeamPositions(teamColor).isEmpty()) {
            for (ChessPosition position : findTeamPositions(teamColor)) {
                if (position.getOccupied().getPieceType().equals(ChessPiece.PieceType.KING)) {
                    return position;
                }
            }
        }
        return null;
//        String message = "King not found";
//        throw new InvalidMoveException(message);
    }

    public TeamColor getOtherTeam() {
        return (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && TeamTurn == chessGame.TeamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, TeamTurn);
    }
}

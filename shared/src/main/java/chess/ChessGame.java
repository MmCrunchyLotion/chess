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
        ChessPiece piece = actualStartPosition.getOccupied();
        TeamColor team = piece.getTeamColor();
        Collection<ChessMove> legalMoves = new ArrayList<>();
        if (piece == null || team != getTeamTurn()) {
            return legalMoves;
        }
        Collection<ChessMove> possibleMoves = actualStartPosition.getOccupied().pieceMoves(board, actualStartPosition);
        for (ChessMove move : possibleMoves) {
            ChessGame gameCopy = this.copy();
            gameCopy.executeMove(move);
            if (!gameCopy.isInCheck(team)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        /* handling a move that uses a new position rather than an existing one */
        ChessPosition startPosition = board.getPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
        ChessPosition endPosition = board.getPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        ChessMove actualMove = new ChessMove(startPosition, endPosition, move.getPromotionPiece());
        Collection<ChessMove> validMoves = validMoves(actualMove.getStartPosition());
        if (validMoves.contains(actualMove)) {
            executeMove(actualMove);
            TeamTurn = (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException();
        }
    }

    public void executeMove(ChessMove move) {
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(),move.getStartPosition().getOccupied());
        } else {
            board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
        }
        move.getStartPosition().setOccupied(null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) /*throws InvalidMoveException*/ {
        ChessPosition kingPosition = findKing(teamColor);
        TeamColor otherTeam = getOtherTeam(teamColor);
        Collection<ChessPosition> otherTeamPositions = findTeamPositions(otherTeam);
        for (ChessPosition position : otherTeamPositions) {
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
        if (!isInCheck(teamColor)) {
            if (findKing(teamColor) == null) {
                return false;
            } else {
                return validMoves(findKing(teamColor)) == null;
            }
        } else {
            return false;
        }
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

    public TeamColor getOtherTeam(TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
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

    public ChessGame copy() {
        ChessGame copy = new ChessGame();
        copy.setBoard(board.copy());
        copy.setTeamTurn(getTeamTurn());
        return copy;
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

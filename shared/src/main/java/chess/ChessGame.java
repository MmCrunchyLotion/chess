package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        ChessPosition actualStartPosition = board.getPosition(startPosition);
        ChessPiece piece = actualStartPosition.getOccupied();
        Collection<ChessMove> legalMoves = new ArrayList<>();
        if (piece == null) {
            return legalMoves;
        }
        TeamColor team = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = actualStartPosition.getOccupied().pieceMoves(board, actualStartPosition);
        for (ChessMove move : possibleMoves) {
            ChessGame gameCopy = this.copy();
            gameCopy.executeMove(gameCopy.getBoard(), move);
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
        ChessPosition startPosition = board.getPosition(move.getStartPosition());
        if (startPosition.getOccupied() == null) {
            throw new InvalidMoveException();
        } else if (startPosition.getOccupied().getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (validMoves.contains(move)) {
            executeMove(board, move);
            TeamTurn = (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException();
        }
    }

    public void executeMove(ChessBoard board, ChessMove move) {
        ChessPosition startPosition = board.getPosition(move.getStartPosition());
        ChessPosition endPosition = board.getPosition(move.getEndPosition());
        ChessPiece piece = startPosition.getOccupied();
        if (move.getPromotionPiece() == null) {
            board.addPiece(endPosition,piece);
        } else {
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        if (!endPosition.getOccupied().isMoved()) {
            endPosition.getOccupied().setMoved(true);
        }
        startPosition.setOccupied(null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        TeamColor otherTeam = getOtherTeam(teamColor);
        Collection<ChessPosition> otherTeamPositions = findTeamPositions(otherTeam);
        for (ChessPosition position : otherTeamPositions) {
            ChessPiece piece = position.getOccupied();
            Collection<ChessMove> moves = piece.pieceMoves(board, position);
            for (ChessMove move : moves) {
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
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return checkTeamMoves(teamColor);
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
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return checkTeamMoves(teamColor);
        }
        return false;
    }

    private boolean checkTeamMoves(TeamColor teamColor) {
        Collection<ChessPosition> teamPositions = findTeamPositions(teamColor);
        for (ChessPosition position : teamPositions) {
            Collection<ChessMove> moves = validMoves(position);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Collection<ChessPosition> findTeamPositions(TeamColor teamColor) {
        Collection<ChessPosition> teamPositions = new ArrayList<>();
        List<ChessPosition> boardPositions = board.getBoard();
        for (int i = 0; i < 64; i++) {
            ChessPosition currentSquare = boardPositions.get(i);
            if (currentSquare.getOccupied() != null && currentSquare.getOccupied().getTeamColor() == teamColor) {
                teamPositions.add(currentSquare);
            }
        }
        return teamPositions;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        Collection<ChessPosition> teamPositions = findTeamPositions(teamColor);
        if (!teamPositions.isEmpty()) {
            for (ChessPosition position : teamPositions) {
                if (position.getOccupied().getPieceType().equals(ChessPiece.PieceType.KING)) {
                    return position;
                }
            }
        }
        return null;
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

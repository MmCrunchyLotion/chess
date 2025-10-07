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
    private ChessMove lastMove = null;

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
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, actualStartPosition);
        checkPossibleMoves(possibleMoves, team, legalMoves);
        return legalMoves;
    }

    private void checkPossibleMoves(Collection<ChessMove> possibleMoves, TeamColor team, Collection<ChessMove> legalMoves) {
        for (ChessMove move : possibleMoves) {
            ChessGame gameCopy = this.copy();
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
                if (Math.abs(colDiff) == 2) {
                    ChessGame intermediateGame = castlingInCheckHandler(team, move, colDiff);
                    if (intermediateGame == null) {
                        continue;
                    }
                    if (intermediateGame.isInCheck(team)) {
                        continue;
                    }
                }
            }
            gameCopy.executeMove(gameCopy.getBoard(), move);
            if (!gameCopy.isInCheck(team)) {
                if (!legalMoves.contains(move)) {
                    legalMoves.add(move);
                }
            }
        }
    }

    private ChessGame castlingInCheckHandler(TeamColor team, ChessMove move, int colDiff) {
        if (isInCheck(team)) {
            return null;
        }
        int row = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();
        int direction = (colDiff > 0) ? 1 : -1;
        ChessGame intermediateGame = this.copy();
        ChessMove intermediateMove = new ChessMove(move.getStartPosition(),intermediateGame.getBoard().getGridPosition(row, startCol + direction),null);
        intermediateGame.executeMove(intermediateGame.getBoard(), intermediateMove);
        return intermediateGame;
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
            lastMove = move;
            TeamTurn = (getTeamTurn() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException();
        }
    }

    public void executeMove(ChessBoard board, ChessMove move) {
        ChessPosition startPosition = board.getPosition(move.getStartPosition());
        ChessPosition endPosition = board.getPosition(move.getEndPosition());
        ChessPiece piece = startPosition.getOccupied();
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            castle(board, move);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            enPassantHandler(board, move, endPosition, startPosition);
        }
        if (move.getPromotionPiece() == null) {
            board.addPiece(endPosition,piece);
        } else {
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        if (endPosition.getOccupied() != null) {
            if (!endPosition.getOccupied().isMoved()) {
                endPosition.getOccupied().setMoved(true);
            }
        }
        startPosition.setOccupied(null);
    }

    private static void enPassantHandler(ChessBoard board, ChessMove move, ChessPosition endPosition, ChessPosition startPosition) {
        int colDiff = Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn());
        if (colDiff == 1 && endPosition.getOccupied() == null) {
            int capturedPawnRow = startPosition.getRow();
            int capturedPawnCol = endPosition.getColumn();
            ChessPosition capturedPawnPosition = board.getGridPosition(capturedPawnRow, capturedPawnCol);
            capturedPawnPosition.setOccupied(null);
        }
    }

    private void castle(ChessBoard board, ChessMove move) {
        int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
        if (colDiff == 2) {
            int row = move.getStartPosition().getRow();
            ChessPosition rookStart = board.getGridPosition(row, 8);
            ChessPosition rookEnd = board.getGridPosition(row, 6);
            ChessPiece rook = rookStart.getOccupied();
            board.addPiece(rookEnd, rook);
            rookEnd.getOccupied().setMoved(true);
            rookStart.setOccupied(null);
        } else if (colDiff == -2) {
            int row = move.getStartPosition().getRow();
            ChessPosition rookStart = board.getGridPosition(row, 1);
            ChessPosition rookEnd = board.getGridPosition(row, 4);
            ChessPiece rook = rookStart.getOccupied();
            board.addPiece(rookEnd, rook);
            rookEnd.getOccupied().setMoved(true);
            rookStart.setOccupied(null);
        }
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

    public ChessMove getLastMove() {
        return lastMove;
    }

    public void setLastMove(ChessMove lastMove) {
        this.lastMove = lastMove;
    }

    public ChessGame copy() {
        ChessGame copy = new ChessGame();
        copy.setBoard(board.copy());
        copy.setTeamTurn(getTeamTurn());
        copy.setLastMove(lastMove);
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

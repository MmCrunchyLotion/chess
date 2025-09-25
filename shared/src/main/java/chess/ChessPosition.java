package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;
    private ChessPiece occupiedBy;

    public ChessPosition(int row, int col) {
        setRow(row);
        setCol(col);
        setOccupied(null);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setOccupied(ChessPiece occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public ChessPiece getOccupied() {
        return this.occupiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // ??? do I need this?
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

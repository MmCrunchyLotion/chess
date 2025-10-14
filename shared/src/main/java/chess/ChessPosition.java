package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int column;
    private ChessPiece occupiedBy;

    public ChessPosition(int row, int column) {
        this.row = row;
        this.column = column;
        this.occupiedBy = null;
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
        return this.column;
    }


    /**
     * Setter places a piece onto this position
     * Getter identifies the piece placed on this position
     */
    public void setOccupied(ChessPiece occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public ChessPiece getOccupied() {
        return this.occupiedBy;
    }

    public ChessPosition copy() {
        ChessPosition copy = new ChessPosition(this.row, this.column);
        if (this.occupiedBy != null) {
            copy.setOccupied(this.occupiedBy.copy());
        } else {
            copy.setOccupied(null);
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
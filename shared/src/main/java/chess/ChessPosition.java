package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;
    private boolean occupied;

    public ChessPosition(int row, int col) {
        if (row < 0 || col < 0 || row > 8 || col > 8) {
            throw new IndexOutOfBoundsException("row or col out of bounds");
        }
        setRow(row);
        setCol(col);
        setOccupied(false);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
//        throw new RuntimeException("Not implemented");
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    public boolean isOccupied() {
        return this.occupied;
    }
}

/************************************************************
 * Name:  Travis Whipple                                    *
 * Project:  Konane 1                                       *
 * Class:  CMPS 331                                         *
 * Date:  2/2/2018                                          *
 * *********************************************************/

package twhipple.konane;


import android.util.Log;

import java.util.Random;
import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;


/******************************************************************
 * Class Name: Board
 *
 * Purpose: Board class implements a board for the Konane game.
 *  This class is where the board is create / altered / and has
 *  functionality retrieve cells with a given ID, tell if two cells
 *  are adjacent, remove a random white and black cell, and find
 *  a cell that is hopped over for a given move.
 *
 * Data Structures: A simple Vector of Cells is used as the Vector
 *  class has a lot of built in functionality such as indexOf,
 *  elementAt and add / remove functionality.
 */
public class Board {

    // Size of board
    private int MAX_WIDTH = 6;

    // All cells are stored dynamically in vector.
    private Vector<Cell> board = new Vector<Cell>();


    // Default constructor makes sure board is clear.
    // No local variables to declare.
    public Board(){
        board.clear();
    }

    public Board(Board a_board){
        board.clear();

        MAX_WIDTH = a_board.MAX_WIDTH;

        Cell tempCell;

        for(Cell cell : a_board.getBoard()){
            tempCell = new Cell(cell);
            board.add(tempCell);
        }
    }

    public void initBoard(){
        // Randomly start board in black or white major form.
        Random rand = new Random();
        boolean isBlackMajor = rand.nextBoolean();
        initBoard(isBlackMajor);
    }

    /******************************************************************
     * Name: initBoard
     *
     * Purpose: This is where the board is initialized to 18 white and
     *  18 black tiles. Each tile alternates between being white and
     *  black. Each row also starts with a different colored tile. Ex:
     *  row one will start with black, and row two will start with white.
     *
     * Local Variables: Temporary Cell that is used to add each cell to
     *  the board.
     *
      */
    public void initBoard(boolean isBlackMajor){

        // Value will alternate between 1 and 0, representing Black and White respectively.
        int colorValue;
        Cell tempCell;

        // Check if board is in Black or White major form.
        if(isBlackMajor){
            colorValue = 1;
        }else{
            colorValue = 0;
        }

        // Will loop for ever cell in board.
        for(int row = 0; row < MAX_WIDTH; row++){
            for(int col = 0; col < MAX_WIDTH; col++){

                // Add a new cell to the board.
                tempCell = new Cell(row, col);

                // Set the Black / White value.
                tempCell.set(colorValue);
                board.add(tempCell);

                // value will alternate between 1 and 0. (Black and White)
                colorValue++;
                colorValue %= 2;
            }

            // value will alternate so each row starts with different colored tiles.
            colorValue++;
            colorValue %= 2;
        }
    }


    /******************************************************************
     *
     * @param cell: Cell to find the index of (Value 0 to number_tiles-1).
     *
     * @return int: Integer of index of the passed cell, -1 if not found.
     */
    public int indexOf(Cell cell){
        return board.indexOf(cell);
    }

    /******************************************************************
     *
     * Purpose: Checks if two cells are adjacent to one another
     *  ( Distance of 2 away from each other )
     *
     * @param idFirst:  ID of first cell to be looked at.
     * @param idSecond: ID of second cell to be looked at.
     *
     * @return: boolean: True if tiles are 2 away from each other.
     *  False if not.
     */
    public boolean isAdjacent(int idFirst, int idSecond){

        // Convert to x, y cords.
        Cell first = findCellById(idFirst);
        Cell second = findCellById(idSecond);

        if(first.getRow() == second.getRow()){
            // Tiles are in same row.
            if(abs(first.getCol() - second.getCol()) == 2){
                return true;
            }
        }

        if(first.getCol() == second.getCol()){
            // Tiles are in same col.
            if(abs(first.getRow() - second.getRow()) == 2){
                return true;
            }
        }

        return false;
    }

    /******************************************************************
     *
     * Purpose: Gets cell with corresponding ID.
     *
     * @param id: ID of cell to find.
     *
     * @return Cell: Cell that has a matching ID. null if not found.
     */
    public Cell findCellById(int id){
        // Loop through all cells in board.
        for(Cell cell : board){
            // If ID matches, return it
            if(cell.getId() == id){
                return cell;
            }
        }

        return null;
    }

    /******************************************************************
     *
     * Purpose: Finds a cell in between two cells. ( Used for making a move ).
     *
     * @param idCellOne: ID of first cell.
     * @param idCellTwo: ID of second cell.
     *
     * @return Cell: Cell in between two adjacent cells ( distance of 2 )
     */
    public Cell findHoppedCell(int idCellOne, int idCellTwo){

        // Hopped cell location.
        int rowOfHopped;
        int colOfHopped;

        // Get cells of passed ID's.
        Cell cellOne = findCellById(idCellOne);
        Cell cellTwo = findCellById(idCellTwo);

        // Get cell in-between two cells clicked.
        if(cellOne.getRow() == cellTwo.getRow()){
            // Both cells are in same row.
            rowOfHopped = cellOne.getRow();

            // Get col of cell to find.
            if(cellOne.getCol() < cellTwo.getCol()){
                colOfHopped = cellOne.getCol() + 1;
            }else{
                colOfHopped = cellTwo.getCol() + 1;
            }
        }else{
            // Both cells are in same col.
            colOfHopped = cellOne.getCol();

            // Get col of cell to find.
            if(cellOne.getRow() < cellTwo.getRow()){
                rowOfHopped = cellOne.getRow() + 1;
            }else{
                rowOfHopped = cellTwo.getRow() + 1;
            }
        }

        // Find cell based on row and col.
        return findCellByLocation(rowOfHopped, colOfHopped);
    }

    /******************************************************************
     *
     * Purpose: Finds a cell from its row and col.
     *
     * @param row: Row of cell to be found.
     * @param col: Col of cell to be found.
     *
     * @return Cell: Cell at corresponding row and col. Null if not found.
     */
    public Cell findCellByLocation(int row, int col){

        if(row < 0 || col < 0){
            return null;
        }

        // Loop through all cells in board.
        for(Cell cell : board){
            // If the current cell's row and col match, return it.
            if(cell.getRow() == row && cell.getCol() == col){
                return cell;
            }
        }

        // Return null if no cell was found.
        return null;
    }


    /******************************************************************
     * Purpose: Removes a random white and black tile from board.
     */
    public void removeRandCells(){
        // Remove random white tile.
        removeRandIsWhite(true);

        // Remove random black tile.
        removeRandIsWhite(false);
    }

    /******************************************************************
     *
     * Purpose: Removes a random white or black tile.
     *
     * @param a_isWhite: Will remove white tile if True, black tile
     *                 if False.
     */
    private void removeRandIsWhite(boolean a_isWhite){

        Random rand = new Random();
        int index = -1;

        // Keep looping until a tile is removed.
        while(true){

            // gets a number 0 - number of tiles.
            index = rand.nextInt(board.size());

            // If cell at that index is of same color and the cell is not empty, remove it.
            if(board.elementAt(index).isWhite() == a_isWhite && board.elementAt(index).isFree() == false){
                board.elementAt(index).setFree(true);
                return;
            }
        }
    }


    /******************************************************************
     *
     * Purpose: To get all elements in board.
     *
     * @return: Vector<Cell>: All cells in board.
     */
    public Vector<Cell> getBoard(){
        Vector<Cell> tempBoard = new Vector<Cell>(board);
        return tempBoard;
    }

    /******************************************************
     * Purpose: Sets board based on a given string matrix. The board can be
     * either 'B W B W B W' or 'W B W B W B' (Spaces should be absent).
     *
     * @param matrix: Matrix of board
     */
    public void setBoardMajor(String matrix){

        char currentChar;

        // Check where the first 'B' or first 'W' is in respect to the board.
        for(int i = 0; i < matrix.length(); i++){
            currentChar = matrix.charAt(i);

            // Check if it is in Black major form.
            if(Character.toUpperCase(currentChar) == 'B'){
                if((i+1) % 2 == 1){
                    initBoard(true);
                    return;
                }else{
                    initBoard(false);
                    return;
                }
            }

            // Check if it is in White major form.
            if(Character.toUpperCase(currentChar) == 'W'){
                if((i+1) % 2 == 1){
                    initBoard(false);
                    return;
                }else{
                    initBoard(true);
                    return;
                }
            }
        }
    }

    /********************************************************
     * Purpose: Sets a board from a given board matrix. Used when
     * loading a serialization file.
     * @param matrix
     */
    public void setBoardFromMatrix(String matrix){

        // Clear board of any existing cells.
        board.clear();

        // Regular expression removes all blank spaces.
        matrix = matrix.replaceAll("\\s+", "");

        int numCells = matrix.length();
        Double width = sqrt(numCells * 1.0);

        String num = width.toString();
        String nums[] = num.split("\\.");
        MAX_WIDTH = Integer.parseInt(nums[0]);

        Log.e("Column", Double.toString(MAX_WIDTH));

        // Set either Black major, or White major.
        setBoardMajor(matrix);

        // Loop variables.
        char currentCell;
        int index = 0;

        // Set
        for(Cell cell : board){

            currentCell = matrix.charAt(index);

            switch(currentCell){
                case 'B':
                    cell.setBlack();
                    break;
                case 'W':
                    cell.setWhite();
                    break;
                case 'O':
                    cell.setFree(true);
                    break;
                case 'E':
                    cell.setFree(true);
                    break;
            }
            index++;
        }
    }

    /********************************************************
     * Purpose: Returns a string containing all cells in board.
     * Ex: "BWBWBWBW"
     *
     * @return: String containing board matrix.
     */
    public String getStringMatrix(){
        String matrix = "";
        int counter = 0;

        // Save each cell in boar as a single character.
        for(Cell cell : board){
            counter++;

            if(cell.isFree()){
                matrix += "O";
            }
            else if(cell.getColorId() == 0){
                matrix += "B";
            }
            else if(cell.getColorId() == 1){
                matrix += "W";
            }
            else{
                // Error.
                matrix += "E";
            }


            if(counter % MAX_WIDTH == 0){
                matrix += "\n";
            }
            else{
                matrix += " ";
            }
        }

        return matrix;
    }

    /**
     *
     * @param boardWidth: Length of board, Aka 6 for a 6X6 board (36 tiles).
     */
    public void setBoardWidth(int boardWidth){

        // Width must be an even number.
        if(boardWidth % 2 != 0){
            return;
        }

        // Width must be greater than 4 and less than 12.
        if(boardWidth <= 4 && boardWidth >= 12){
            return;
        }

        MAX_WIDTH = boardWidth;
    }

    public int getBoardWidth(){
        return MAX_WIDTH;
    }

    public String toString(){
        return getStringMatrix();
    }
}

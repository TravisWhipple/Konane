package twhipple.konane;

/******************************************************************
 *
 * Class Name: Cell
 *
 * Purpose: Stores all functionality and properties of a cell for
 *  the game Konane.
 *
 */
public class Cell {


    private String id;
    private int colorId;

    private int row;
    private int col;

    private boolean isEmpty;
    private boolean isBlack;
    private boolean isWhite;

    // Color values.
    private int BLACK = 0;
    private int WHITE = 1;


    /******************************************************************
     *
     * @param a_row: Row of cell to be created.
     * @param a_col: Col of cell to be created.
     */
    public Cell(int a_row, int a_col){

        row = a_row;
        col = a_col;

        // ID has row and col of cell in format: (row-cell) both starting at 1.
        id = Integer.toString(row+1);
        id += "-";
        id += Integer.toString(col+1);

        // Current tile has no color yet.
        colorId = -1;

        // Set the current tile to not have any piece.
        setFree(true);
    }

    /********************************************************
     * Purpose: Copy constructor.
     * @param a_cell: Cell to be copied.
     */
    public Cell(Cell a_cell){
        row = a_cell.getRow();
        col = a_cell.getCol();

        id = a_cell.getIdString();

        colorId = a_cell.getColorId();


        isBlack = a_cell.isBlack;
        isWhite = a_cell.isWhite;
        isEmpty = a_cell.isEmpty;

        if(!isWhite || !isBlack){
            //isEmpty = true;
        }
    }

    /** * * * *            Getters           * * * * * * *
     *
     */
    /******************************************************************
     *
     * @return: True if tile is free (has no piece), False if not.
     */
    public boolean isFree(){ return isEmpty; }

    /******************************************************************
     *
     * @return: True if tile is a black tile, False if not.
     */
    public boolean isBlack(){ return isBlack; }


    /******************************************************************
     *
     * @return: True if tile is a white tile, False if not.
     */
    public boolean isWhite(){ return isWhite; }

    /******************************************************************
     *
     * @return: Gets ID of tile in string format: row-col, EX: "1-2"
     */
    public String getIdString(){ return id; }

    /******************************************************************
     *
     * @return: Gets the ID of the current cell as an integer value.
     */
    public int getId(){return (row*10) + col;}

    /******************************************************************
     *
     * @return: The row of the Cell.
     */
    public int getRow(){ return row; }

    /******************************************************************
     *
     * @returnL The column of the Cell.
     */
    public int getCol(){ return col; }

    /******************************************************************
     *
     * @return: The colorId of the current cell. White and Black have
     *  two different values defined at top.
     */
    public int getColorId(){return colorId;}





    /** *  * * * * *            Setters           * * * * * * *

    /******************************************************************
     *
      * @param a_isFree: Boolean: True if cell is free, False if not.
     */
    public void setFree(boolean a_isFree){
        isEmpty = a_isFree;
    }

    /******************************************************************
     * Setters for setBlack and setWhite call private set function.
     */
    public void setBlack(){ setWhite(false); }
    public void setWhite(){ setWhite(true); }

    /******************************************************************
     *
     * Purpose: Private set function is to guarantee setters work.

     *
     * @param a_isWhite: True if cell is to be white. False if to be black.
     */
    private void setWhite(boolean a_isWhite){
        isEmpty = false;
        isWhite = a_isWhite;
        isBlack = !a_isWhite;

        if(isWhite){
            colorId = WHITE;
        }else{
            colorId = BLACK;
        }
    }

    /******************************************************************
     *
     * @param val: Set color of current cell.
     */
    public void set(int val){
        if(val == 0){
            setWhite();
        }else{
            setBlack();
        }
    }

    public String toString(){

        String str = "";
        if(isEmpty){
            str += "    isEmpty";
        }else{
            str += "           ";
        }
        if(isWhite){
            str += "    isWhite";
        }else{
            str += "           ";
        }
        if(isBlack){
            str += "    isBlack";
        }else{
            str += "           ";
        }


        return id + str + "                        ";
    }

}

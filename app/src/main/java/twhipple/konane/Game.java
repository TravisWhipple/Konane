package twhipple.konane;

import android.util.Log;

import java.util.Random;
import java.util.Vector;

import static java.lang.Math.abs;

/**
 * Created by traviswhipple on 1/27/18.
 */

/******************************************************************
 *
 * Class Name: Game
 *
 * Purpose: This is where all the game logic for Konane is. Each
 *  is validated before made, there are two players that alternate
 *  playing, one black and one white.
 */

public class Game {

    protected Board board;

    // Both players of game.
    private Player[] player;
    //private Player whitePlayer;
    //private Player blackPlayer;

    private Player human;
    private Player computer;

    // Alternates between white and black player.
    private int currentPlayer;

    private String[] playerName = {"Human", "Computer"};

    //private int BLACK = 0;
    //private int WHITE = 1;

    private int HUMAN = 0;
    private int COMPUTER = 1;

    // Saves previous cell clicked to know where to move a cell from.
    private int idOfPrevCell;
    // True only if a cell has already been selected.
    private boolean hasSelectedCell;

    // Message containing description of move made.
    private String message;

    // If player can make adjacent move, they have to.
    private boolean canMakeAdjacentMove;
    private int adjacentMove;



    private final int UP = 1;
    private final int RIGHT = 2;
    private final int DOWN = 3;
    private final int LEFT = 4;

    private boolean isComputerTurn;

    private boolean isGuessingColor;

    /******************************************************************
     * Purpose: Default constructor initialized board for a game of
     *  Konane.
     */
    public Game(){
        // Initialize board and players.
        board = new Board();

        /* TODO, left off changing white player to human and what not.
        need to find a way to make the players human and computer or whatever.
         */

        human = new Player();
        //human.setColorWhite();

        computer = new Player();
        //computer.setColorBlack();

        player = new Player[2];
        player[HUMAN] = human;
        player[COMPUTER] = computer;

        currentPlayer = -1;

        initializeStartState();
    }

    public boolean isComputerTurn(){

        if(currentPlayer == COMPUTER){
            return true;
        }

        return false;
    }


    /***
     * Purpose: Copy constructor.
     * @param a_game: Game to be copied.
     */
    public Game(Game a_game){
        board = new Board(a_game.getBoardNew());

        human = new Player(a_game.human);
        //human.setColorWhite();

        computer = new Player(a_game.computer);
        //computer.setColorBlack();


        player              = new Player[2];
        player[HUMAN]       = human;
        player[COMPUTER]    = computer;

        idOfPrevCell        = a_game.idOfPrevCell;
        hasSelectedCell     = a_game.hasSelectedCell;
        message             = a_game.message;
        canMakeAdjacentMove = a_game.canMakeAdjacentMove;
        adjacentMove        = a_game.adjacentMove;
        currentPlayer       = a_game.currentPlayer;

        isGuessingColor     = a_game.isGuessingColor;
    }

    /**
     *
     * @return: Current board of the game.
     */
    public Board getBoardNew(){
        return board;
    }

    /**
     * Initializes all starting values to their defaults.
     */
    private void initializeStartState(){

        currentPlayer = -1;

        // No cell has been selected yet.
        idOfPrevCell = -1;
        hasSelectedCell = false;

        // No moves have been made yet.
        canMakeAdjacentMove = false;
        adjacentMove = -1;

        isGuessingColor = true;
    }


    public boolean humanRandTileGuess(int idOfGuessedCell){

        // Check if human guessed the right cell.
        if(findCellById(idOfGuessedCell).isFree() && findCellById(idOfGuessedCell).isBlack()){
            player[HUMAN].setColorBlack();
            player[COMPUTER].setColorWhite();
            currentPlayer = HUMAN;
            isGuessingColor = false;
            return true;
        }else{
            player[HUMAN].setColorWhite();
            player[COMPUTER].setColorBlack();
            currentPlayer = COMPUTER;
            isGuessingColor = false;
            return false;
        }
    }

    public boolean isGuessingColor(){
        return isGuessingColor;
    }

    /***
     * Purpose: Set the current player from the players name.
     * @param a_currentPlayer: Name of player.
     */
    public void setCurrentPlayer(String a_currentPlayer){

        if(a_currentPlayer == playerName[HUMAN]){
            currentPlayer = HUMAN;
        }else{
            currentPlayer = COMPUTER;
        }

    }


    /******************************************************************
     *
     * @return: Current white players score.
     */
    public int getComputerScore(){
        return player[COMPUTER].getScore();
    }

    /******************************************************************
     *
     * @return: Current black players score.
     */
    public int getHumanScore(){
        return player[HUMAN].getScore();
    }

    /******************************************************************
     *
     * @return: Name of current player. Either Black or White.
     */
    public String getCurrentPlayerName(){

        if(currentPlayer != -1){
            return playerName[currentPlayer];
        }else{
            return "";
        }
    }

    public int getCurrentPlayerId(){
        return currentPlayer;
    }

    public int getCurrentPlayerColorId(){
        if(currentPlayer == -1){
            return -1;
        }
        return player[currentPlayer].getColorId();
    }

    public String getBoardMatrix(){ return board.getStringMatrix(); }

    public int getBoardWidth(){
        return board.getBoardWidth();
    }

    /******************************************************************
     *
     * @return: Id of the previous cell selected.
     */
    public int getIdOfPrevCell(){
        return idOfPrevCell;
    }

    /******************************************************************
     *
     * @param a_idOfPrevCell: ID of previous cell selected.
     * @param idOfCell: ID of current cell selected.
     *
     * @return: True if valid move, False if not.
     */
    public boolean isLegalMove(int a_idOfPrevCell, int idOfCell){

        // Save current state.
        idOfPrevCell = a_idOfPrevCell;
        boolean hasSelectedCellPrev = hasSelectedCell;
        int idOfPrevCellPrev = idOfPrevCell;
        idOfPrevCell = a_idOfPrevCell;
        hasSelectedCell = true;
        boolean canMakeAdjacentMovePrev = canMakeAdjacentMove;
        //canMakeAdjacentMove = false;



        boolean retVal = isLegalMove(idOfCell);

        // Return current state back to what it was.
        canMakeAdjacentMove = canMakeAdjacentMovePrev;
        hasSelectedCell = hasSelectedCellPrev;
        idOfPrevCell = idOfPrevCellPrev;
        return retVal;

    }

    /***************************************************************
     * Purpose: This loads a given serialization file. It sets all
     * member variables of the game to the proper state.
     *
     * @param blackPlayerScore: Score to set as black players.
     * @param whitePlayerScore: Score to set as white players.
     * @param boardMatrix: Single dimensional representation of the board.
     * @param nextPlayer: Name of the next player.
     * @return
     */
    public boolean loadSerializationData(int blackPlayerScore, int whitePlayerScore, String boardMatrix, String nextPlayer, String humanColor){

        // Wipe all values, start from scratch.
        initializeStartState();

        boolean returnValue = false;

        human.setScore(blackPlayerScore);
        computer.setScore(whitePlayerScore);
        board.setBoardFromMatrix(boardMatrix);

        // Check if next player is black.
        if(nextPlayer == ""){
            isGuessingColor = true;
            currentPlayer = -1;
        }else if(nextPlayer.toLowerCase().contains("black")){

            if(humanColor.toLowerCase().contains("black")){
                currentPlayer = HUMAN;
            }else{
                currentPlayer = COMPUTER;
            }
        }else{
            if(humanColor.toLowerCase().contains("white")){
                currentPlayer = HUMAN;
            }else{
                currentPlayer = COMPUTER;
            }
        }


        isGuessingColor = false;

        Log.e("Human", "Color");
        Log.e("Color:", humanColor);

        if(humanColor == ""){
            isGuessingColor = true;

        } else if(humanColor.toLowerCase().contains("black")){
            human.setColorBlack();
            computer.setColorWhite();

        }else{
            human.setColorWhite();
            computer.setColorBlack();
        }

        return returnValue;
    }

    /******************************************************************
     *
     * Purpose: After a cell is selected, look at the previous cell
     *  that was selected and see if there is a legal move between the
     *  two cells. Message is set to inform user of if a move was made
     *  or why a move can not be made.
     *
     * @param idOfCell: Id of cell that was selected.
     *
     * @return: True if a legal move exists, False if only one tile
     *  was selected or if move is not legal.
     */
    public boolean isLegalMove(int idOfCell){

        // If they selected the same tile, do not check for move.
        if(idOfPrevCell == idOfCell){
            message = playerName[currentPlayer] + " Player has selected a tile.";
            return false;
        }

        // Check if player can make an adjacent move.
        if(canMakeAdjacentMove){

            if(idOfPrevCell != adjacentMove){
                message = playerName[currentPlayer]
                        + " Player has to continue move on "
                        + board.findCellById(adjacentMove).getIdString()
                        + ".";
                return false;
            }

            if(!findCellById(idOfCell).isFree()){
                message = playerName[currentPlayer]
                        + " Player has to continue move on "
                        + board.findCellById(adjacentMove).getIdString()
                        + ".";
                return false;
            }
        }


        // Check if player is moving their own piece. (Matching color).
        if(player[currentPlayer].getColorId() == findCellById(idOfCell).getColorId()){
            // Then the current player has selected one of their own cells.

            // Check if this is the first cell they selected, or if they are making a move.
            if(hasSelectedCell){

                // If they select another tile that is not free, make that their previous tile.
                if(!board.findCellById(idOfCell).isFree()){
                    message = playerName[currentPlayer] + " Player has selected a tile.";
                    idOfPrevCell = idOfCell;
                    return false;
                }


                // If cell is adjacent to the previous cell, it may be a legal move.
                if(board.isAdjacent(idOfPrevCell, idOfCell)){

                    // Get hoped over tile.
                    Cell hopped = board.findHoppedCell(idOfPrevCell, idOfCell);

                    // Cannot hop over an empty cell.
                    if(hopped.isFree()) {
                        message = "Not valid move, can not hop over empty tiles";

                    }else if(!board.findCellById(idOfCell).isFree()){
                        // Can only hop to an empty cell.
                        message = "Not valid move, can only hop to an empty tile.";

                    }else if(player[currentPlayer].getColorId()  == board.findCellById(idOfCell).getColorId()){
                        // Player can only move on correct colored tiles.

                        message = playerName[currentPlayer] + " Player moved to gain one point.";

                        // If player makes more than one hop in a move.
                        if(canMakeAdjacentMove){
                            message += " Again!";
                        }
                        return true;
                    }
                }else{
                    message = "Not valid move, can only move tiles adjacent to each other";
                }

            }else{

                // This is the first cell in their move.
                if(board.findCellById(idOfCell).isFree()){
                    message = playerName[currentPlayer] + " Player must select a non empty tile.";
                    return false;
                }

                // Player has only selected a tile, not made a move.
                message = playerName[currentPlayer] + " Player has selected a tile.";
                hasSelectedCell = true;
                idOfPrevCell = idOfCell;
                return false;
            }

        }else{
            // Player has selected a cell that is not their own.
            message = playerName[currentPlayer] + " Player must play their own tile. (" + player[currentPlayer].getColor() + " tile)";
        }

        // Player has not made a valid move.
        hasSelectedCell = false;
        idOfPrevCell = -1;

        // Check if player has to make an adjacent move.
        if(canMakeAdjacentMove){
            message = playerName[currentPlayer] + " Player has to continue move on " + board.findCellById(adjacentMove).getIdString() + ".";
            idOfPrevCell = adjacentMove;
            hasSelectedCell = true;
        }

        // All valid moves return true, only here if not valid move.
        return false;
    }


    /******************************************************************
     *
     * @return: Message describing most recent move.
     */
    public String getMessage(){
        return message;
    }

    /******************************************************************
     *
     * @param idOfCell: ID of cell to make a move to.
     *
     * @return: True if move was made, False if move could not be made.
     */
    public boolean makeMove(int idOfCell){

        // Check that move is legal.
        if(isLegalMove(idOfCell)){

            // Get hopped over cell.
            Cell hopped = board.findHoppedCell(idOfPrevCell, idOfCell);

            // Remove piece from hopped cell.
            hopped.setFree(true);

            // Set previous cell to be free
            board.findCellById(idOfPrevCell).setFree(true);

            // Set current cell from free to not free anymore.
            board.findCellById(idOfCell).setFree(false);


            // Add points to player.
            player[currentPlayer].addPoints(1);
            player[currentPlayer].setHasSkipped(false);

            // Check if player has an adjacent move.
            if(hasAdjacentMove(idOfCell)){

                // Set previous cell to current so used does not have to re-click it.
                idOfPrevCell = idOfCell;
                hasSelectedCell = true;
                canMakeAdjacentMove = true;
                adjacentMove = idOfCell;

            }else{

                // No adjacent moves possible.
                moveToNextPlayer();

                // Check if player has any valid moves.
                if(!hasValidMoves()){
                    // Player skips if no valid moves.
                    player[currentPlayer].setHasSkipped(true);
                    moveToNextPlayer();

                    // Check again if next player has any possible moves
                    // ( If both players cannot move, game is over ).
                    if(!hasValidMoves()){
                        player[currentPlayer].setHasSkipped(true);
                        moveToNextPlayer();

                        isGameOver();
                    }

                }
            }

            // Move was successful.
            return true;
        }else{
            // Move was not successful.
            return false;
        }
    }

    /******************************************************************
     *
     * @return: 1 or 0 for either white or black player.
     */
    public void moveToNextPlayer(){

        idOfPrevCell = -1;
        hasSelectedCell = false;
        canMakeAdjacentMove = false;

        if(currentPlayer == HUMAN){
            currentPlayer = COMPUTER;
        }else{
            currentPlayer = HUMAN;
        }
    }

    /******************************************************************
     *
     * @param idOfCell: ID of cell to be determined if adjacent move
     *                exists.
     *
     * @return: True if exists, false if not.
     */
    public boolean hasAdjacentMove(int idOfCell){

        if(idOfCell == -1){
            return false;
        }

        // Save current state, as calling isValidMove will over write it.
        String tempMessage = message;
        int tempPrevCell = idOfPrevCell;
        boolean tempHasSelected = hasSelectedCell;

        boolean tempCanMakeAdjacentMove = canMakeAdjacentMove;
        canMakeAdjacentMove = false;

        idOfPrevCell = idOfCell;

        boolean returnVal = false;

        int row = findCellById(idOfCell).getRow();
        int col = findCellById(idOfCell).getCol();


        // Try to move in all 4 directions.

        // Try to move up:
        Cell tempCell = findCellByLocation(row-2, col);
        if(tempCell != null && isLegalMove(idOfCell, tempCell.getId())){
            returnVal = true;
        }

        // Try to move down:
        tempCell = findCellByLocation(row+2, col);
        if(tempCell != null && isLegalMove(idOfCell, tempCell.getId())){
            returnVal = true;
        }

        // Try to move left:
        tempCell = findCellByLocation(row, col-2);
        if(tempCell != null && isLegalMove(idOfCell, tempCell.getId())){
            returnVal = true;
        }

        // Try to move right:
        tempCell = findCellByLocation(row, col+2);
        if(tempCell != null && isLegalMove(idOfCell, tempCell.getId())){
            returnVal = true;
        }

        // Return saved state.
        message = tempMessage;
        idOfPrevCell = tempPrevCell;
        hasSelectedCell = tempHasSelected;
        canMakeAdjacentMove = tempCanMakeAdjacentMove;

        return returnVal;
    }

    /**
     *
     * @return True if player can make a second move in one turn.
     */
    public boolean playerCanContinue(){
        return canMakeAdjacentMove;
    }

    /**
     *
     * @return Id of the cell that the player has to continue
     * their adjacent move with.
     */
    public int getAdjacentMove(){
        return adjacentMove;
    }

    /**
     *
     * @return True if player can press continue button, only can
     * when they have already made a previous move.
     */
    public boolean canContinue(){

        if(hasAdjacentMove(idOfPrevCell)){
            return true;
        }
        return false;
    }

    /**
     * Purpose: Player does not want to continue with adjacent moves,
     * they will skip to the next player.
     * @return
     */
    public boolean makeContinueMove(){
        if(canContinue()){
            moveToNextPlayer();
            return true;
        }

        return false;
    }

    /******************************************************************
     *
     * @return: Return True if player has valid moves, False if not.
     */
    public boolean hasValidMoves(){

        // Loop through all cells in board.
        for(Cell cell : board.getBoard()){

            // Check if current cell matches players color.
            if(player[currentPlayer].getColorId() == cell.getColorId()){

                // If cell is not free and has an adjacent move.
                if(!cell.isFree() && hasAdjacentMove(cell.getId())){
                    return true;
                }
            }
        }

        // If no move was found, player as to skip their turn.
        message += "\n" + playerName[currentPlayer] + " had to skip their turn!";
        player[currentPlayer].setHasSkipped(true);
        return false;
    }

    /******************************************************************
     *
     * @return: True if game is over, False if not.
     *  Game is over when both players can no longer make any moves.
     */
    public boolean isGameOver(){

        // Game is over if both players have skipped their turn.
        if(player[HUMAN].getHasSkipped() && player[COMPUTER].getHasSkipped()){
            return true;
        }

        return false;
    }

    /******************************************************************
     *
     * @param id: ID of cell to find.
     *
     * @return: Cell with the given ID.
     */
    public Cell findCellById(int id){
        return board.findCellById(id);
    }

    public Cell findCellByLocation(int a_row, int a_col){
        return board.findCellByLocation(a_row, a_col);
    }

    /******************************************************************
     *
     * @return: Name of the winner of the game.
     *  White, Black or Tie.
     */
    public String getWinnerName(){

        // Check if Black player has more points.
        if(player[HUMAN].getScore() > player[COMPUTER].getScore()){
            // Black player wins.
            return playerName[HUMAN];

        }else if (player[HUMAN].getScore() < player[COMPUTER].getScore()){
            // White player wins.
            return playerName[COMPUTER];

        }else{
            // Tie
            return "Tie";
        }
    }

    /******************************************************************
     *
     * @return: Score of the winner. -1 If a tie game.
     */
    public int getWinnerScore(){

        // Check who has higher score.
        if(player[HUMAN].getScore() > player[COMPUTER].getScore()){
            // Black player wins.
            return player[HUMAN].getScore();

        }else if (player[HUMAN].getScore() < player[COMPUTER].getScore()){
            // White player wins.
            return player[COMPUTER].getScore();

        }else{
            // Tie
            return -1;
        }
    }

    /******************************************************************
     *
     * @return: Name of loser of game.
     */
    public String getLoserName(){

        // Check who the winner is, and return the opposite name.
        if(getWinnerName() == playerName[HUMAN]){
            // Black player won, return White name.
            return playerName[COMPUTER];

        }else if(getWinnerName() == playerName[COMPUTER]){
            // White player won, return Black name.
            return playerName[HUMAN];
        }else{
            return "Tie";
        }
    }

    /******************************************************************
     *
     * @return: Score of loser
     */
    public int getLoserScore(){
        if(getWinnerName() == playerName[HUMAN]){
            return player[COMPUTER].getScore();
        }else {
            // White won, or is a tie.
            return player[HUMAN].getScore();
        }
    }

    /******************************************************************
     * Purpose: Sets board up for game.
     */
    public void initialize(){

        // Game will be in either Black or White major form.
        board.initBoard();

        // Remove two random tiles.
        board.removeRandCells();
    }

    public boolean isCellBlack(Cell guessedCell){
        if(guessedCell.isBlack() && guessedCell.isFree()){
            return true;
        }else{
            return false;
        }
    }

    /******************************************************************
     *
     * @return: All cells in board.
     */
    public Vector<Cell> getBoard(){
        return board.getBoard();
    }

    /**
     *
     * @return True if player can make an adjacent move.
     */
    public boolean canMakeAdjacentMove(){
        return canMakeAdjacentMove;
    }


    /**
     * @param cell: Cell to make move with.
     * @return True if move was successful.
     */
    public boolean makeMoveUp(Cell cell){
        return makeMove(cell, -2, 0);
    }
    public boolean makeMoveDown(Cell cell){
        return makeMove(cell, 2, 0);
    }
    public boolean makeMoveLeft(Cell cell){
        return makeMove(cell, 0, -2);
    }
    public boolean makeMoveRight(Cell cell){
        return makeMove(cell, 0, 2);
    }

    /**
     * Purpose: Make a move depending on which direction was specified.
     * @param cell: Cell to be moved.
     * @param rowScalar: Cell to move to's row distance.
     * @param colScalar: Cell to move to's col distance.
     * @return: True if move was successful.
     */
    public boolean makeMove(Cell cell, int rowScalar, int colScalar){
        hasSelectedCell = true;
        idOfPrevCell = cell.getId();
        // Find adjacent cell from given cell.
        cell = board.findCellByLocation(cell.getRow() + rowScalar, cell.getCol() + colScalar);
        return makeMove(cell.getId());
    }

    /**
     * Purpose: Test if a cell can move in a given direction.
     * @param cell: Cell to test if can be moved.
     * @return: True if cell can be moved in a given direction.
     */

    public boolean canMove(Cell cell, int direction){

        return canMoveDirection(cell, rowScalar(direction), colScalar(direction));
    }

    public boolean makeMove(Cell cell, int direction){
        return makeMove(cell, rowScalar(direction), colScalar(direction));
    }

    private int rowScalar(int moveDirection){
        if(moveDirection == UP){
            return -2;
        }else if (moveDirection == DOWN){
            return 2;
        }else{
            return 0;
        }
    }

    private int colScalar(int moveDirection){
        if(moveDirection == LEFT){
            return -2;
        }else if (moveDirection == RIGHT){
            return 2;
        }else{
            return 0;
        }
    }

    public boolean canMoveUp(Cell cell){
        return canMoveDirection(cell, -2, 0);
    }
    public boolean canMoveDown(Cell cell){
        return canMoveDirection(cell,2, 0);
    }
    public boolean canMoveLeft(Cell cell){
        return canMoveDirection(cell, 0, -2);
    }
    public boolean canMoveRight(Cell cell){
        return canMoveDirection(cell, 0, 2);
    }


    /**
     *
     * @param cell: Cell to test if can be moved in a given direction.
     * @param rowScalar: Row distance from given cell to desired cell to move to.
     * @param colScalar: Col distance from given cell to desired cell to move to.
     * @return
     */
    public boolean canMoveDirection(Cell cell, int rowScalar, int colScalar){

        int idOfCell = cell.getId();

        String tempMessage = message;
        int tempPrevCell = idOfPrevCell;
        boolean tempHasSelected = hasSelectedCell;

        boolean returnVal = false;

        // Get row and column of given cell.
        int row = findCellById(idOfCell).getRow();
        int col = findCellById(idOfCell).getCol();

        // Find adjacent cell from row and col scalars.
        Cell tempCell = board.findCellByLocation(row + rowScalar, col + colScalar);

        // Try to move in given direction:
        if(tempCell != null && isLegalMove(idOfCell, tempCell.getId())){
            returnVal = true;
        }

        // Return saved state.
        message = tempMessage;
        idOfPrevCell = tempPrevCell;
        hasSelectedCell = tempHasSelected;

        // Return if move is valid.
        return returnVal;
    }

    /**
     * Purpose: Gets cell adjacent (Closest same color tile) to a given cell.
     * @param cell: Given cell to find its adjacent cell.
     * @return: Cell that was found.
     */
    public Cell getAdjacentCellUp(Cell cell){
        return getAdjacentCellInDirection(cell, -2, 0);
    }
    public Cell getAdjacentCellDown(Cell cell){
        return getAdjacentCellInDirection(cell, 2, 0);
    }
    public Cell getAdjacentCellLeft(Cell cell){
        return getAdjacentCellInDirection(cell, 0, -2);
    }
    public Cell getAdjacentCellRight(Cell cell){
        return getAdjacentCellInDirection(cell, 0, 2);
    }

    /**
     * Purpose: Gets cell adjacent (Closest same color tile) to a given cell.
     * @param cell: Given cell to look for its neighbor.
     * @param rowScalar: Row distance from cell to its neighbor cell.
     * @param colScalar: Col distance from cell to its neighbor cell.
     * @return
     */
    public Cell getAdjacentCellInDirection(Cell cell, int rowScalar, int colScalar){
        int row = cell.getRow();
        int col = cell.getCol();

        return findCellByLocation(row + rowScalar, col + colScalar);
    }


    public String getHumanColor(){
        return human.getColor();
    }

    public String getComputerColor(){
        return human.getColor();
    }
}
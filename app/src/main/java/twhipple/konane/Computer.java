package twhipple.konane;

/**
 * Created by traviswhipple on 3/20/18.
 */

public class Computer extends Player {


    // Steps computer takes to make a move.

    // First get the best move in a tree.

    // Make that move.

    // Next player goes.

    // Local variables.
    private int score;

    // Color of player.
    private boolean isWhite;
    private boolean isBlack;

    private boolean hasSkipped;

    private Game game;
    private Ai ai;

    AiNode moveToMake;

    /******************************************************************
     * Purpose: Default constructor, starts player off fresh.
     */
    //public Computer(Game a_game){
    //    initComputer(a_game);
    //}

    private void initComputer(Game a_game){
        score = 0;
        isWhite = false;
        isBlack = false;
        hasSkipped = false;
        game = a_game;
        ai = new Ai(a_game);
        moveToMake = new AiNode();
    }

    public AiNode getMove(Game game, int plyCutoff){
        ai.setGame(game);
        moveToMake = ai.minMax(plyCutoff);
        return moveToMake;
    }

    public AiNode getPrunedMove(Game game, int plyCutoff){
        ai.setGame(game);
        moveToMake = ai.minMaxPruning(plyCutoff);
        return moveToMake;
    }

    public Game makeMove(){
        return moveToMake.game;
    }

    /******************************************************************
     * Purpose: Sets the players color to white.
     */
    public void setColorWhite(){
        isWhite = true;
        isBlack = false;
    }

    /******************************************************************
     * Purpose: Sets the players color to black.
     */
    public void setColorBlack(){
        isWhite = false;
        isBlack = true;
    }

    public void setScore(int a_score){
        score = a_score;
    }

    /******************************************************************
     *
     * @param a_hasSkipped: Set if the player has skipped their turn or
     *                    not. True if has skipped, False if has not skipped.
     */
    public void setHasSkipped(boolean a_hasSkipped){
        hasSkipped = a_hasSkipped;
    }

    /******************************************************************
     *
     * @return: If the player ahs skipped their last turn or not.
     */
    public boolean getHasSkipped(){
        return hasSkipped;
    }

    /******************************************************************
     *
     * @param points: Adds X amount of points to the current players score.
     */
    public void addPoints(int points){
        score += points;
    }

    /******************************************************************
     *
     * @return: Current players score.
     */
    public int getScore(){
        return score;
    }

}

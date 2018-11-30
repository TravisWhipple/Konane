package twhipple.konane;

/**
 * Created by traviswhipple on 2/1/18.
 */

/******************************************************************
 * Class Name: Player
 *
 * Purpose: This is where a player for Konane is implemented. Each
 *  player has a color, score and whether or not they skipped their
 *  last turn.
 */
public class Player {

    // Local variables.
    private int score;

    // Color of player.
    private boolean isWhite;
    private boolean isBlack;

    private boolean isHuman;
    private boolean isComputer;

    private boolean hasSkipped;

    private String colorName;
    private final String WHITE = "White";
    private final String BLACK = "Black";

    /******************************************************************
     * Purpose: Default constructor, starts player off fresh.
     */
    public Player(){
        initPlayer();
    }

    public Player(Player a_player){
        score = a_player.score;

        isWhite = a_player.isWhite;
        isBlack = a_player.isBlack;

        isHuman = a_player.isHuman;
        isComputer = a_player.isComputer;

        hasSkipped = a_player.hasSkipped;

        colorName = a_player.colorName;
    }

    private void initPlayer(){
        score = 0;

        isWhite = false;
        isBlack = false;

        colorName = "";

        isHuman = false;
        isComputer = false;

        hasSkipped = false;
    }

    /******************************************************************
     * Purpose: Sets the players color to white.
     */
    public void setColorWhite(){
        isWhite = true;
        isBlack = false;
        colorName = WHITE;
    }

    /******************************************************************
     * Purpose: Sets the players color to black.
     */
    public void setColorBlack(){
        isWhite = false;
        isBlack = true;
        colorName = BLACK;
    }

    public void setScore(int a_score){
        score = a_score;
    }

    public String getColor(){
        return colorName;
    }

    public int getColorId(){

        if(isBlack){
            return 0;
        }else{
            return 1;
        }
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

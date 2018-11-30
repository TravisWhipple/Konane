package twhipple.konane;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/******************************************************************
 * Class Name: Konane
 *
 * Purpose: This is where the game Konane is displayed to the user
 *  using Android GUI. The board is laid out using buttons that the
 *  user can click to make a move. The current player, players scores
 *  and history of moves made is displayed on the screen.
 */
public class Konane extends AppCompatActivity{

    public GridLayout gridLayout;
    private TextView currentPlayerView;

    private TextView humanPlayerScoreView;
    private TextView computerPlayerScoreView;

    private LinearLayout moveDescriptionLayout;
    private Button continueButton;

    private TextView aiGainedPoints;
    private Spinner algorithmSpinner;

    private Serialization serialize;
    public Game game;

    private Ai ai;

    private Button computerMakeMove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.konane);

        game        = new Game();
        serialize   = new Serialization(game);
        ai          = new Ai(game);

        Intent intent = getIntent();
        String strWidth = intent.getStringExtra("boardWidth");
        game.board.setBoardWidth(Integer.parseInt(strWidth));

        // Initialize local views to their corresponding XML objects.
        currentPlayerView       = findViewById(R.id.currentPlayerView);
        humanPlayerScoreView    = findViewById(R.id.humanPlayerScoreView);
        computerPlayerScoreView = findViewById(R.id.computerPlayerScoreView);
        moveDescriptionLayout   = findViewById(R.id.moveDescriptionLayout);

        continueButton          = findViewById(R.id.continueButton);
        aiGainedPoints          = findViewById(R.id.aiGainedPoints);
        aiGainedPoints.setText("");

        computerMakeMove        = findViewById(R.id.computerMakeMove);


        // Spinner allows user to select from different search algorithms.
        algorithmSpinner        = findViewById(R.id.algorithmSpinner);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.algorithms, android.R.layout.simple_spinner_item);
        algorithmSpinner.setAdapter(arrayAdapter);
        algorithmSpinner.setBackgroundColor(getResources().getColor(R.color.orange));

        // Set search algorithm based on what is selected.
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                TextView myText = (TextView) view;
                int algoNum = 0;
                String selected = myText.getText().toString();

                Log.e("ALGO", "ITEM SELECTED");

                // Check if user selected Depth First Search.
                if(selected.contains("Depth")){
                    Log.e("ALGO", "DEPTH FIRST SEARCH");
                    algoNum = 1;
                }

                // Check if user selected Breadth First Search.
                if(selected.contains("Breadth")){
                    Log.e("ALGO", "BREDTH FIRST SEARCH");
                    algoNum = 2;
                }

                // Check if user selected Best First Search.
                if(selected.contains("Best")){
                    algoNum = 3;
                }

                // Check if user selected Branch and Bound.
                if(selected.contains("Branch")){
                    algoNum = 4;
                    getBranchNum();
                }

                // Set selected algorithm
                ai.setAlgorithm(algoNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        gridLayout = findViewById(R.id.gridView);
        gridLayout.setBackgroundColor(getResources().getColor(R.color.black_background));

        // Start game of konane.
        startGame();
    }

    /****************************************************
     * Purpose: Get the depth for branch and bound.
     */
    public void getBranchNum(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Input is where user will input the depth they want.
        final EditText input = new EditText(this);
        input.setHint("Depth");
        input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Alert dialog to get user input.
        alertDialog
                .setTitle("Enter Depth For Tree")
                .setMessage("Non Zero Integer, -1 for whole tree")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int userDepth = 0;

                        // Will catch if user enters a non integer number.
                        try{

                            userDepth = Integer.parseInt(input.getText().toString());

                            if(userDepth != 0){

                                ai.setDepthLimit(userDepth);
                                ai.search();

                            }else{
                                // User cannot enter zero.
                                toastAlert("Enter a non-zero number");
                            }

                        }catch (NumberFormatException e){
                            // User can only enter a number, not text.
                            toastAlert("Please enter a number");
                        }
                    }
                });

        alertDialog.create();
        alertDialog.show();
    }

    /****************************************************
     * Purpose: Creates a toast message to inform user of a given alert.
     * @param alert: Message to appear on toast.
     */
    private void toastAlert(String alert){

        Context context = getApplicationContext();

        // Set duration to long to ensure user reads it.
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, alert, duration);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /******************************************************************
     * Purpose: Starts a game of Konane. First initializes game, then
     *  renders all on screen attributes.
     */
    public void startGame(){
        game.initialize();
        updateViews();
        updateMoveDescription("Select which tile you think is black");
    }

    /******************************************************************
     * Purpose: Updates all buttons on board after each move. Gets all
     *  cells in board and for each cell creates a button with correct
     *  color background and tile if cell has a tile or not.
     */

    public void updateButtons(){
        updateButtons(null);
    }

    /****************************************************
     * Purpose: Allow to be called with a passed AiNode. This node is
     * the current AiNode in the search tree. This node will help us
     * draw out the current path the given algorithm traverses.
     *
     * @param possibleMove: Current move in tree.
     */
    public void updateButtons(AiNode possibleMove){

        // Clear gridlayout so we can re-add buttons.
        gridLayout.removeAllViews();

        // Get the width of the board.
        int boardWidth = game.getBoardWidth();

        // Set the height and width of the board.
        gridLayout.setColumnCount(boardWidth);
        gridLayout.setRowCount(boardWidth);

        int gridViewWidth = 540;
        int cellSize = gridViewWidth / boardWidth;


        // Add every cell in board as a button.
        for(Cell cell : game.getBoard()){

            ImageButton tempButton = new ImageButton(this);
            String text = cell.getIdString();


            // Set layout parameters for each cell.
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;

            // Set size of each cell.
            lp.width = cellSize;
            lp.height = cellSize;


            // Dynamically set size of image in each button to fill button.
            tempButton.setScaleType(ImageView.ScaleType.CENTER);
            tempButton.setAdjustViewBounds(true);

            // Add above parameters to each button in the gridView.
            tempButton.setLayoutParams(lp);

            // Each button will have unique id associated to its row and col location.
            tempButton.setId(cell.getId());



            // First check if player is guessing black removed tile.
            if(game.isGuessingColor()){
                // Set all tiles to same color so player has no indication as to which
                // randomly removed tile is black and white.
                if(cell.isFree()){
                    tempButton.setBackgroundColor(getResources().getColor(R.color.white));
                }else{
                    tempButton.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                }

                // When clicked, will call 'clicked' function.
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clicked(view);
                    }
                });

                // Add button to grid.
                gridLayout.addView(tempButton);
                continue;
            }



            // Set selected tile to blue.
            if(cell.getId() == game.getIdOfPrevCell()){
                tempButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }


            // Set background color of each cell and add proper image piece. (black or white)
            if(cell.isWhite()){

                // Set cell to white tile.
                tempButton.setBackgroundColor(getResources().getColor(R.color.lightGray));

                // Add piece if cell is not free.
                if(!cell.isFree()) {
                    tempButton.setImageDrawable(getResources().getDrawable(R.drawable.white_circle));
                }

            }else{
                // Set cell to black tile.
                tempButton.setBackgroundColor(getResources().getColor(R.color.darkGray));

                // Add piece if cell is not free.
                if(!cell.isFree()){
                    tempButton.setImageDrawable(getResources().getDrawable(R.drawable.black_circle));
                }
            }

            if(possibleMove != null){

                // If the current cell in the board happens to be of the the tiles that the
                // search algorithm has already moved on, make it orange.
                if(possibleMove.previousTilesPlayedContains(cell.getId())){
                    tempButton.setBackgroundColor(getResources().getColor(R.color.orange));
                }

                // Draw an arrow over hopped cells.
                if(!cell.isFree()){

                    // Used to layer an arrow over a cell's piece.
                    Drawable[] layers = new Drawable[2];

                    if(tempButton.getBackground() == null){
                        tempButton.setImageDrawable(getResources().getDrawable(R.drawable.empty));
                    }

                    // Save the current background of the tile (White or Black circle).
                    layers[0] = tempButton.getDrawable();
                    layers[1] = tempButton.getDrawable();

                    // For cell that was hopped, get the move direction that was made.
                    switch (possibleMove.getHoppedTile(cell.getId())){

                        case "UP":
                            layers[1] = getResources().getDrawable(R.drawable.arrow_up);
                            break;

                        case "DOWN":
                            layers[1] = getResources().getDrawable(R.drawable.arrow_down);
                            break;

                        case "LEFT":
                            layers[1] = getResources().getDrawable(R.drawable.arrow_left);
                            break;

                        case "RIGHT":
                            layers[1] = getResources().getDrawable(R.drawable.arrow_right);
                            break;
                    }

                    // Add layers to button.
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    tempButton.setImageDrawable(layerDrawable);
                }

                // Set color of cell algorithm wants to move to, to blue.
                if(possibleMove.getCellMovedTo().getId() == cell.getId()){
                    tempButton.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                }
            }

            // When clicked, will call 'clicked' function.
            tempButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(view);
                }
            });

            // Add button to grid.
            gridLayout.addView(tempButton);
        }
    }

    /******************************************************************
     *
     * Purpose: Called every time a button is clicked in the board.
     *
     * @param view: View object that called this function ( ImageButton ).
     */
    // Called when a button is clicked.
    public void clicked(View view){

        updateGainedScore(null);

        // View is an ImageButton because that is what was clicked.
        ImageButton buttonPressed = (ImageButton) view;

        if(game.isGuessingColor()){

            if(game.humanRandTileGuess(buttonPressed.getId())){
                updateMoveDescription("Human guessed correctly!");
            }else{
                updateMoveDescription("Human guessed incorrectly!");
            }

            updateViews();
            return;
        }

        if(game.isComputerTurn()){
            updateMoveDescription("Computers must make its own move.");
            return;
        }

        // Check if there are valid moves in game.
        if(!game.isComputerTurn() && game.hasValidMoves()){

            // Check if move is legal.
            if(game.isLegalMove(buttonPressed.getId())){

                // Make legal move.
                game.makeMove(buttonPressed.getId());

                // Update view to display move made.
                updateViews();
                ai.search();
            }
        }

        updateMoveDescription(game.getMessage());

        // Check if game is over or not.
        if(game.isGameOver() == true){
            endGame();
        }
    }


    /******************************************************************
     * Purpose: Updates all buttons on board, players scores and current player.
     */
    public void updateViews(){
        updateButtons();

        // Update score
        updateScores();

        // Update current player
        updateCurrentPlayer();


        if(game.playerCanContinue()){
            continueButton.setVisibility(View.VISIBLE);
        }else{
            continueButton.setVisibility(View.INVISIBLE);
        }

        updatePlayerColors();

        updateTime(null);
    }

    /****************************************************
     * Purpose: Adds a message to the move description (Should be a move made).
     * @param message
     */
    public void updateMoveDescription(String message){

        TextView tempText = new TextView(this);
        tempText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempText.setTextColor(getResources().getColor(R.color.white));

        tempText.setTextSize(25);

        // Get message describing above move.
        String text = message;

        tempText.setText(text);

        // Add view to front so most recent move is at the top of move description.
        moveDescriptionLayout.addView(tempText, 0);
    }

    /******************************************************************
     * Purpose: Updates both players scores to be their current scores.
     */
    public void updateScores(){

        // Set black players score.
        humanPlayerScoreView.setText(Integer.toString(game.getHumanScore()));

        // Set white players score.
        computerPlayerScoreView.setText(Integer.toString(game.getComputerScore()));
    }


    /******************************************************************
     * Purpose: Adds a white or black tile next to each player to indicate
     * what color tile they are playing.
     */
    public void updatePlayerColors(){

        ImageView humanPlayerTileColor = findViewById(R.id.humanPlayerTileColor);
        ImageView computerPlayerTileColor = findViewById(R.id.computerPlayerTileColor);

        // Colors have not been assigned yet.
        if(game.getHumanColor() == ""){
            humanPlayerTileColor.setImageResource(android.R.color.transparent);
            computerPlayerTileColor.setImageResource(android.R.color.transparent);
            return;
        }


        if(game.getHumanColor().toLowerCase().contains("black")){
            humanPlayerTileColor.setImageResource(R.drawable.black_circle);
            computerPlayerTileColor.setImageResource(R.drawable.white_circle);
        }else{
            humanPlayerTileColor.setImageResource(R.drawable.white_circle);
            computerPlayerTileColor.setImageResource(R.drawable.black_circle);
        }
    }

    /******************************************************************
     * Purpose: Updates who the current player is displayed at the top.
     */
    public void updateCurrentPlayer(){

        currentPlayerView.setText("Current Player: " + game.getCurrentPlayerName());

        // Players have not been assigned a color yet.
        if(game.getCurrentPlayerId() == -1){
            computerMakeMove.setVisibility(View.INVISIBLE);
            return;
        }

        // Make button visible.
        computerMakeMove.setVisibility(View.VISIBLE);

        // Change text depending on who's turn it is.
        if(game.isComputerTurn()){
            //computerMakeMove.setVisibility(View.VISIBLE);
            computerMakeMove.setText("Computer Make Move");
        }else{
            computerMakeMove.setText("Help");
            //computerMakeMove.setVisibility(View.INVISIBLE);
        }
    }

    /******************************************************************
     * Purpose: Called when the game is over. This will create an alert
     *  that when when acknowledge, will return the game to the main menu.
     */
    public void endGame(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Game Over");

        String winner = game.getWinnerName();
        int winnerScore = game.getWinnerScore();

        String loser = game.getLoserName();
        int loserScore = game.getLoserScore();

        String winnerText = "";

        if(winnerScore == -1){
            // Then there was a tie.
            winnerText = "Tie game!";
            winnerText += "\n\n";
            winnerText += "Black Player Score: " + game.getHumanScore() + "\n";
            winnerText += "White Player Score: " + game.getComputerScore();
        }else{
            winnerText = winner + " Player won the game with " +
                    Integer.toString(winnerScore) + " points!";
            winnerText += "\n\n";
            winnerText += loser + " scored " + loserScore + " points.";
        }

        alertDialog.setMessage(winnerText);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Exit to main menu",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

    /****************************************************
     * Purpose: Called when player wants to continue, instead of making an adjacent move.
     * @param v: View that called this function.
     */
    public void continueButton(View v){

        // Check if move is legal.
        if(game.canContinue()){

            // Make legal move.
            game.makeContinueMove();

            // Update view to display move made.
            updateViews();
        }
    }

    /****************************************************
     * Purpose: Updates the predicted gained score based off search algorithm.
     * @param move: Current move in algorithm.
     */
    public void updateGainedScore(AiNode move){

        // If left empty, nothing will be written to screen.
        String pointsGained = "";

        // Check if there is a move.
        if(move != null){
            int points = move.getDepth();
            pointsGained = "Points Gained: " + Integer.toString(points);
        }

        // Draw gained points to screen.
        aiGainedPoints.setText(pointsGained);
        updateButtons(move);
    }

    /****************************************************
     * Purpose: Called when player presses one of the buttons associated with
     * the search algorithms.
     *
     * @param view
     */
    public void artificialIntelligence(View view){

        // Get which button was clicked.
        switch (view.getId()){

            case R.id.createTree:

                // Create the search tree.
                ai.search();
                updateGainedScore(null);
                isFirstClick = true;
                break;

            case R.id.nextMove:

                // Get the next possible move.
                updateGainedScore(ai.getNextMove());
                break;

            case R.id.bestMove:

                // Get the best move in the given tree.
                updateGainedScore(ai.getBestMove());
                //computerMakeMove(view);
                break;

            case R.id.clearAi:

                // Clear the board of the moves.
                updateGainedScore(null);
                break;

            case R.id.computerMakeMove:
                long startTime = System.currentTimeMillis();
                updateGainedScore(computerMakeMove(view));
                long finishTime = System.currentTimeMillis() - startTime;
                if(!isFirstClick){
                    updateTime(finishTime, ai.getNodesVisited());
                }
                Log.e("Time:", Long.toString(finishTime));
                break;
        }
    }


    private boolean isFirstClick = true;
    private AiNode moveToMake = null;

    /******************************************************************
     * Purpose: What is called when player presses button for computer
     * to make a move.
     *
     * @param v:    The view that called this. (Button).
     * @return:     A move that the computer will make.
     */
    public AiNode computerMakeMove(View v){

        if(v.getId() == R.id.computerMakeMove && isFirstClick){
            //plyCutoff();
        }

        CheckBox checkBox = findViewById(R.id.alphaBetaPruningCheckBox);


        EditText plyCutoff = findViewById(R.id.plyCutoff);
        int cutoffNum = Integer.parseInt(plyCutoff.getText().toString());

        ai.setGame(game);

        // First show the move to make.
        if(isFirstClick){

            ai.setGame(game);

            // Check if alpha beta pruning.
            if(checkBox.isChecked()){
                // Search for move.
                moveToMake = ai.minMaxPruning(cutoffNum);

            }else{
                // Search for move.
                moveToMake = ai.minMax(cutoffNum);
            }

            updateButtons(moveToMake);

            isFirstClick = false;
            return moveToMake;
        }

        isFirstClick = true;
        game = moveToMake.game;

        ai.setGame(game);
        updateViews();

        // Check if game is over or not.
        if(game.isGameOver() == true){
            endGame();
        }

        return null;
    }

    /******************************************************************
     * Purpose: Updates the time minimax took.
     *
     * @param timeTaken: Time taken for minimax to return a value.
     */
    public void updateTime(Long timeTaken){
        updateTime(timeTaken, null);
    }


    public void updateTime(Long timeTaken, Integer numNodesVisited){
        TextView textView = findViewById(R.id.timeTaken);

        if(timeTaken == null){
            textView.setText("");
            return;
        }

        // Get time in Hours, Minutes, Seconds, Milliseconds.
        Long milli = timeTaken % 1000;
        Long seconds = timeTaken / 1000;

        String time = "";

        // Get minutes
        if(seconds > 60){
            Long minutes = seconds / 60;
            seconds = seconds % 60;

            // Get hours
            if(minutes > 60){
                Long hours = minutes / 60;
                minutes = minutes % 60;
                time += hours + "h, ";
            }

            time += minutes + "m, ";
        }

        time += seconds + "." + milli + "s";
        String text = "Time: " + time;

        if(numNodesVisited != null){
            text += ", Nodes Visited: " + Integer.toString(numNodesVisited);
        }

        textView.setText(text);
    }

    /**
     * Purpose: Brings up alert dialog to have user enter file name to save game.
     * @param v: View that called this function, can be ignored.
     */
    public void saveGame(View v){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint("File Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog
                .setTitle("Save Game")

                .setMessage("Enter a name for your file.")

                .setNegativeButton("Save and Quit to Main Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Save to the given file name.
                        serialize.saveToFile(input.getText().toString(), game);

                        // Exit back to the main menu.
                        finish();
                    }
                })

                .setPositiveButton("Save and Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Save to the given file name.
                        serialize.saveToFile(input.getText().toString(), game);
                    }
                });

        alertDialog.create();
        alertDialog.show();
    }

    /**
     * Purpose: Brings up alert dialog to have user enter file name to load game.
     * @param v: View that called this function, can be ignored.
     */
    public void loadGame(View v){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint("File Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog
                .setTitle("Load Game")

                .setMessage("Enter the name of your file.")

                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Load", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Check if file was loaded correctly.
                        if(serialize.loadFile(input.getText().toString())){
                            dialogInterface.dismiss();

                            // Clear all previous moves.
                            moveDescriptionLayout.removeAllViews();

                            // Update views with new data.
                            updateViews();

                            // Update Ai.
                            ai.search();

                        }else{
                            toastAlert("File could not be loaded, please try again");
                        }
                    }
                });

        alertDialog.create();
        alertDialog.show();
    }
}

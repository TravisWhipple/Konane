package twhipple.konane;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.abs;

/******************************************************************
 *
 * Class Name: Serialization
 *
 * Purpose: This Class saves and loads game files. It can save
 * the current game's state to a txt file in a pre-determined
 * format. This file can be named by the user to later be loaded
 * in.
 */
public class Serialization extends AppCompatActivity {

    private String fileName;
    private Game game;


    private String data = "Hello this is the test data";
    private String path;

    /**
     * Purpose: Constructor. Takes current game to either save from,
     * or load a game into it.
     * @param a_game: Current game state.
     */
    public Serialization(Game a_game){

        // Save game.
        game = a_game;

        // Used to create a random file name.
        Random rand = new Random();

        // Create file with random 3 digit number.
        fileName = "saveFile_" + Integer.toString(abs(rand.nextInt()%1000 + 1)) + ".txt";
        Log.e("FileName", fileName);

        /* Create a path to save games to the file "aKonane"
        using this file name because it will stay at the top of the list of files
        making it easier to access for debugging.
        */
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aKonane";
        File dir = new File(path);
        dir.mkdirs();
    }

    /**
     * Purpose: Loads a file with the default random file name.
     * @return: True if file was successfully loaded.
     */
    public boolean loadFile(){
        return loadFile(fileName);
    }

    /**
     * Purpose: Loads a file from a given file name.
     *
     * @param a_fileName: Name of file to be loaded.
     * @return
     */
    public boolean loadFile(String a_fileName){

        // Save file.
        setFileName(a_fileName);

        // String containing the whole file.
        String rawData = getRawData();

        // Error loading file if raw data is empty.
        if(rawData == ""){
            return false;
        }

        // Spits raw data on end line character.
        String data[] = rawData.split("\n");

        // Set up local variables to default values.
        int blackPlayerScore = 0;
        int whitePlayerScore = 0;
        String boardMatrix = "";
        String nextPlayer = "";
        String humanColor = "Black";

        // Loop through all lines of the file.
        for(String line : data){

            // Looking for black players score.
            if(line.contains("Black:")){
                line = line.replace("Black: ", "");
                blackPlayerScore = Integer.parseInt(line);
                continue;
            }

            // Looking for white player score.
            if(line.contains("White:")){
                line = line.replace("White: ", "");
                whitePlayerScore = Integer.parseInt(line);
                continue;
            }

            // Get current player of game.
            if(line.contains("Next Player:")){
                line = line.replace("Next Player: ", "");
                nextPlayer = line;
                continue;
            }

            // Get Humans color.
            if(line.contains("Human:")){
                line = line.replace("Human: ", "");
                humanColor = line;
                continue;
            }

            if(line.contains("Board:")){
                continue;
            }

            boardMatrix += line;

            /*
            // Skipping over data we already have.
            if(line.contains("Black") || line.contains("White") || line.contains("Board")){
                // Do nothing.
            }else{
                boardMatrix += line;
            }
            */
        }

        // Load parsed data into game.
        game.loadSerializationData(blackPlayerScore, whitePlayerScore, boardMatrix, nextPlayer, humanColor);
        return true;
    }

    /**
     * Purpose: Retrieves data from given file and returns it as a single string.
     * @return: String of file contents.
     */
    private String getRawData(){

        // Will return empty string if file could not be loaded.
        String rawData = "";

        // Get file to load through given path.
        File myFile = new File(path + "/" + fileName);
        FileInputStream inputStream = null;

        // Check if file exists or not.
        if(!myFile.exists()){
            return "";
        }

        try {
            inputStream = new FileInputStream(myFile);

            // Set up buffer.
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            // Read into buffer
            inputStream.read(buffer);
            inputStream.close();

            // Save contents of buffer.
            rawData = new String(buffer);
            Log.e("LOAD", "Success");
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("LOAD", "Fail");
            Log.e("ERR", e.toString());
        }

        // Return raw data.
        return rawData;
    }

    /**
     * Purpose: Saves current game to a file in a pre-determined format.
     * @param a_fileName: Name of file to save to.
     * @return: True if save was successful.
     */
    public boolean saveToFile(String a_fileName, Game a_game){

        game = a_game;

        // Save the name of the file.
        setFileName(a_fileName);

        if(isExternalStorageWritable() == false){
            Log.e("ERR", "Cannot write to SD");
            return false;
        }

        // Data to save to the file. This is the game's data
        // in the correct serialization format.
        String data = dataInCorrectFormat();

        // Get file in path.
        File myFile = new File(path + "/" + fileName);
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(myFile);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.e("SAVE", "Saved under: " + fileName);
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("SAVE", "ERROR, Below");
            Log.e("ERR", e.toString());
            return false;
        }
    }

    /**
     * Purpose: Puts data into correct format for serialization.
     * @return: String containing all file data.
     */
    private String dataInCorrectFormat(){

        /* Example format of file:
        Black: 6
        White: 4
        Board:
        B W B W B W
        W B O B O B
        B W O W B W
        O B O O O O
        B W B O O W
        W B W O O B
        Next Player: White
        Human: White
         */

        String data = "";

        // Save player scores.
        data += "Black: " + game.getHumanScore();
        data += "\n";
        data += "White: " + game.getComputerScore();
        data += "\n";

        // Save board.
        data += "Board:";
        data += "\n";
        data += game.getBoardMatrix();

        // Save next player.
        data += "Next Player: ";
        data += game.getCurrentPlayerName();
        data += "\n";

        // Save humans color.
        data += "Human: ";
        data += game.getHumanColor();
        data += "\n";

        return data;
    }

    /*****
     * Checks if external storage is able to read and write from.
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Purpose: Saves a given file name to a member variable.
     * Also checks if file has '.txt' at the end and adds it
     * if not.
     *
     * @param a_FileName: Name of file to save/load from.
     */
    public void setFileName(String a_FileName){

        // Check if file has .txt
        if(a_FileName.contains(".txt")){
            fileName = a_FileName;
        }else{
            fileName = a_FileName + ".txt";
        }
    }

    /**
     *
     * @return: File's name.
     */
    public String getFileName(){
        return fileName;
    }
}

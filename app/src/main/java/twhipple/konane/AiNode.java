package twhipple.konane;

import java.util.Vector;

/********************************************************
 * Class Name: AiNode
 *
 * Purpose: This Class implements a node for an Ai tree.
 * Each node in the tree has a parent and children, along with a depth,
 * and if the node was visited. All children are stored in a vector.
 */

public class AiNode{

    protected Game game;

    private boolean hasParent;

    private boolean wasVisited;

    private String moveMade;
    private int moveDirection;

    private int currentDepth;
    private int cellMoved;

    private Vector<AiNode> children = new Vector<AiNode>();
    private AiNode parent;

    private int heuristic;



    /********************************************************
     * Default constructor.
     */
    public AiNode(){
        wasVisited = false;
    }

    /********************************************************
     * Copy constructor.
     *
     * @param node: Node to be copied.
     */
    public AiNode(AiNode node){
        game = new Game(node.game);

        wasVisited = false;
        moveMade = "";
        heuristic = 0;

        moveDirection = node.moveDirection;
        moveMade = node.moveMade;
        cellMoved = node.cellMoved;

        //todo maybe messing up code.
        //game.initialize();

        //parent = node.parent;

        //for(AiNode child : node.children){
        //    children.add(child);
        //}

    }

    /********************************************************
     * Purpose: Constructor creates a new AiNode.
     *
     * @param a_game: Game state for given AiNode.
     */
    public AiNode(Game a_game){
        //game = new Game();
        game = a_game;

        wasVisited = false;
        moveMade = "";
        heuristic = 0;
    }

    /********************************************************
     *
     * @param move: Move made, either up, down, left, right.
     */
    public void setMoveMade(String move){
        moveMade = move;
        moveDirection = getMoveDirectionInt(move);
    }

    public int getMoveDirectionInt(String move){
        move = move.toUpperCase();

        switch (move){
            case "UP":
                return 1;
            case "RIGHT":
                return 2;
            case "DOWN":
                return 3;
            case "LEFT":
                return 4;
        }

        return -1;
    }

    /********************************************************
     *
     * @param a_cellMoved: Cell that was moved.
     */
    public void setCellMoved(int a_cellMoved){
        cellMoved = a_cellMoved;
    }

    /********************************************************
     *
     * @return: Cell that was moved.
     */
    public int getCellMoved(){
        return cellMoved;
    }

    /********************************************************
     *
     * @return: Cell that was moved to based on move made.
     */
    public Cell getCellMovedTo(){

        Cell cellMovedTo = null;

        // Get adjacent cell in direction of move.
        switch (moveMade){
            case "UP":
                cellMovedTo = game.getAdjacentCellUp(game.findCellById(cellMoved));
                break;

            case "DOWN":
                cellMovedTo = game.getAdjacentCellDown(game.findCellById(cellMoved));
            break;

            case "LEFT":
                cellMovedTo = game.getAdjacentCellLeft(game.findCellById(cellMoved));
            break;

            case "RIGHT":
                cellMovedTo = game.getAdjacentCellRight(game.findCellById(cellMoved));
            break;

            default:
                cellMovedTo = game.findCellById(cellMoved);
                break;
        }

        return cellMovedTo;
    }

    /********************************************************
     *
     * @param parentNode: Node of parent.
     */
    public void setParent(AiNode parentNode){
        parent = parentNode;
        hasParent = true;
    }

    /********************************************************
     *
     * @param child: Child to be added.
     */
    public void addChild(AiNode child){
        child.setParent(this);

        // Check if node is already a child.
        if(!children.contains(child)){
            children.add(child);
        }

        // Set child's depth and heuristic.
        child.addDepth(currentDepth);
    }




    public int getHeuristicScore(int currentPlayerId) {

        if (currentPlayerId == 0) {
            return (game.getHumanScore() - game.getComputerScore());
        } else {
            return (game.getComputerScore() - game.getHumanScore());
        }

    }
    public void setHeuristic(int a_heuristic){
        heuristic = a_heuristic;
        a_heuristic += 1;

        if(parent != null && parent.heuristic < a_heuristic) {
            parent.setHeuristic(a_heuristic);
        }
    }

    /********************************************************
     *
     * @param index: Index of child to get.
     * @return: Child node at that given index.
     */
    public AiNode getChildAt(int index){
        if(index < getNumChildren()){
            return children.elementAt(index);
        }

        return null;
    }

    /********************************************************
     * Removes all children.
     */
    public void removeChildren(){
        children.clear();
    }

    /********************************************************
     *
     * @return: Number of children current node has.
     */
    public int getNumChildren(){
        return children.size();
    }

    /********************************************************
     *
     * @return: Vector of children.
     */
    public Vector<AiNode> getChildren(){
        return children;
    }

    /********************************************************
     *
     * @param a_wasVisited: True if cell was visited already.
     */
    public void setWasVisited(boolean a_wasVisited){
        wasVisited = a_wasVisited;
    }

    /********************************************************
     *
     * @return: True if cell was visited, false if not.
     */
    public boolean wasVisited(){
        return wasVisited;
    }

    /********************************************************
     *
     * @return: Vector of nodes of parent and parent's parent...
     * all the way to the root node.
     */
    public Vector<AiNode> getFamilyLine(){

        AiNode currentNode = this;
        Vector<AiNode> familyLine = new Vector<AiNode>();

        while(currentNode.hasParent){
            familyLine.add(currentNode);
            currentNode = currentNode.parent;
        }

        return familyLine;
    }

    /********************************************************
     *
     * @param cellId: If one of the previous moves played
     *              on a given tile.
     *
     * @return: True if it does, false if not.
     */
    public boolean previousTilesPlayedContains(int cellId){
        for(AiNode node : getFamilyLine()){
            if(node.getCellMoved() == cellId){
                return true;
            }
        }
        return false;
    }

    /********************************************************
     *
     * @param cellId: Possible hoped cell.
     * @return: Move made on a given hopped tile, empty string
     * if not found.
     */
    public String getHoppedTile(int cellId){
        for(AiNode node: getFamilyLine()){
            if(game.board.findHoppedCell(node.cellMoved, node.getCellMovedTo().getId()).getId() == cellId){
                return node.moveMade;
            }
        }
        return "";
    }

    /********************************************************
     *
     * @return: String containing move made in format:
     *  1-1 down to 3-1
     *  (Currently row and col are flipped. Will fix soon.
     *  //TODO fix row and col.
     */
    public String toString(){

        if(game.findCellById(cellMoved) == null || moveMade == null || getCellMovedTo() == null){
            return "First Move";
        }

        return game.findCellById(cellMoved).getIdString()
                + " " + moveMade + " to "
                + getCellMovedTo().getIdString() + "    "
                + game.getCurrentPlayerName() + " turn, score: "
                + "Human: " + game.getHumanScore() + ", Computer: " + game.getComputerScore();
    }

    /********************************************************
     *
     * @param depth: Sets current depth to one more than depth passed.
     */
    public void addDepth(int depth){
        currentDepth = depth + 1;
    }

    /********************************************************
     *
     * @return: Current depth in tree.
     */
    public int getDepth(){
        return currentDepth;
    }




}


























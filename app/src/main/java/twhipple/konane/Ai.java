package twhipple.konane;


import android.util.Log;

import java.util.Vector;


/******************************************************************
 *
 * Class Name: Ai
 *
 * Purpose: This class is where search algorithms are implemented
 * to search through all possible moves of a player and report
 * back the next possible move based on a specified search algorithm.
 */

public class Ai {

    private Vector<AiNode> tempStack;
    private Vector<AiNode> finalStack;


    private int treeDepth;
    private int locationInStack;

    private int depthLimit;
    private AiNode headNode;
    private int algorithm;

    private Game game;

    private boolean isPruning = false;
    private int count;


    /********************************************************
     *
     * @param a_game: Current state of the game.
     */
    public Ai(Game a_game){
        isPruning = false;

        game = new Game();
        game = a_game;

        headNode = new AiNode(a_game);
        finalStack = new Vector<>();
        tempStack = new Vector<>();
        locationInStack = 0;
        depthLimit = Integer.MAX_VALUE;
    }

    public void setGame(Game a_game){
        game = a_game;
    }

    /********************************************************
     * Purpose: Creates a tree of possible moves to be traversed by a search algorithm.
     *
     * Algorithm: For each cell in board, try to move in all directions, U, R, D, L,
     * then if is legal move, make that move and save as a child of current node. Keep
     * going until there are no more legal moves to make. Calls self recursively on each
     * legal move.
     *
     * @param currentNode: Current node in tree.
     * @param depth: Distance away from depth cutoff.
     * @param currentDepth: Current depth in tree.
     */
    public void tree(AiNode currentNode, int depth, int currentDepth){

        // Reached depth cutoff.
        if(depth == 0){
            return;
        }

        // Set depth to be the maximum value.
        if(depth == -1){
            depth = Integer.MAX_VALUE;
        }

        // Set tree depth, this is the overall deepest part of the tree.
        if(currentDepth > treeDepth){
            treeDepth = currentDepth;
        }


        for(Cell cellInQuestion: currentNode.game.getBoard()){
            // Loop through all cells in board.

            // Continue if cell is free or if cell is not of current players color.
            if(cellInQuestion.isFree() || cellInQuestion.getColorId() != currentNode.game.getCurrentPlayerColorId()){
                continue;
            }

            // Continue if can make continued move and current cell is not the continued move cell.
            if(currentNode.game.canMakeAdjacentMove() && currentNode.game.getAdjacentMove() != cellInQuestion.getId()){
                continue;
            }

            // Try to move up:
            if(currentNode.game.canMoveUp(cellInQuestion)){
                makeAdjacentMove(currentNode, cellInQuestion, depth, currentDepth, "UP");
            }

            // Try to move right:
            if(currentNode.game.canMoveRight(cellInQuestion)){
                makeAdjacentMove(currentNode, cellInQuestion, depth, currentDepth, "RIGHT");
            }

            // Try to move down:
            if(currentNode.game.canMoveDown(cellInQuestion)){
                makeAdjacentMove(currentNode, cellInQuestion, depth, currentDepth, "DOWN");
            }

            // Try to move Left:
            if(currentNode.game.canMoveLeft(cellInQuestion)){
                makeAdjacentMove(currentNode, cellInQuestion, depth, currentDepth, "LEFT");
            }

        }

    }

    /********************************************************
     * Purpose: Adds a cell to the tree then checks if can make adjacent move.
     *
     * @param currentNode: Current node in tree.
     * @param cellInQuestion: Current cell in board.
     * @param depth: Current distance from depth cutoff.
     * @param currentDepth: Current depth in tree.
     * @param moveDirection: Direction to moved in.
     */
    private void makeAdjacentMove(AiNode currentNode, Cell cellInQuestion, int depth, int currentDepth, String moveDirection){

        // Add current node to tree.
        AiNode newChild = addToTree(currentNode, cellInQuestion, moveDirection);

        // Check if child can make an adjacent move.
        if(newChild.game.canMakeAdjacentMove()){
            //currentNode = newChild;
            tree(newChild, depth-1, currentDepth+1);
        }
    }

    /********************************************************
     * Purpose: Makes a given move, then adds current node to tree.
     *
     * @param currentNode: Current node in tree.
     * @param cellInQuestion: Cell to move.
     * @param moveDirection: Direction to move cell.
     * @return
     */
    private AiNode addToTree(AiNode currentNode, Cell cellInQuestion, String moveDirection){

        // Create a new temporary game.
        Game tempGame = new Game(currentNode.game);
        AiNode newChild;

        // Make a move in the temp game.
        switch (moveDirection){
            case "UP":
                tempGame.makeMoveUp(cellInQuestion);
                break;

            case "RIGHT":
                tempGame.makeMoveRight(cellInQuestion);
                break;

            case "DOWN":
                tempGame.makeMoveDown(cellInQuestion);
                break;

            case "LEFT":
                tempGame.makeMoveLeft(cellInQuestion);
                break;
        }

        // Create a new node with the new game.
        newChild = new AiNode(tempGame);
        newChild.setMoveMade(moveDirection);
        newChild.setCellMoved(cellInQuestion.getId());

        // Add node to be child of the current node in tree.
        currentNode.addChild(newChild);

        return newChild;
    }

    /********************************************************
     * Creates tree with no depth cutoff.
     */
    public void createTree(){
        createTree(-1);
    }

    /********************************************************
     * Creates a tree with a given depth cutoff.
     *
     * @param depth: Depth cutoff to use.
     */
    public void createTree(int depth){
        headNode.removeChildren();
        headNode.game = game;
        locationInStack = 1;
        treeDepth = 0;

        tree(headNode, depth, 1);
    }

    /**
     * Searches depending on which algorithm is set.
     */
    public void search(){

        // If algorithm is not 1-4 set it to DFS by default.
        if(algorithm < 1 || algorithm > 4){
            algorithm = 1;
            return;
        }

        switch (algorithm){
            case 1:
                DFS();
                break;
            case 2:
                BFS();
                break;
            case 3:
                BestFirstSearch();
                break;
            case 4:
                BranchAndBound();

        }
    }

    /********************************************************
     * Purpose: Set algorithm to used from integer value,
     * algorithm numbers are as follows:
     *      1. Depth First Search
     *      2. Breadth First Search
     *      3. Best First Search
     *      4. Branch and Bound
     *
     * @param a_algorithm: Number of algorithm to use.
     */
    public void setAlgorithm(int a_algorithm){
        algorithm = a_algorithm;
        search();
    }

    /********************************************************
     * Call recursive depth first search function with
     * headNode as starting node.
     */
    public void DFS(){
        createTree();
        finalStack.clear();
        DFS(headNode, finalStack);
    }

    /********************************************************
     * Purpose: Recursive Depth First Search algorithm.
     *
     * Algorithm (recursive):
     *      First set current node visited and save it.
     *      Then for all its children:
     *          If Child was not visited then call recursively on child.
     *
     * @param currentNode: Current node to be evaluated.
     */
    public void DFS(AiNode currentNode, Vector<AiNode> stack){

        // Current node was visited.
        currentNode.setWasVisited(true);

        // Save current node.
        stack.add(currentNode);

        // Loop through children.
        for(AiNode node : currentNode.getChildren()){

            // Check if child was visited.
            if(!node.wasVisited()){
                DFS(node, stack);
            }
        }
    }

    /********************************************************
     * Call recursive Breadth First Search function with
     * headNode as starting node.
     */
    public void BFS(){
        createTree();
        finalStack.clear();

        Vector<AiNode> newStack = new Vector<>();
        tempStack.clear();

        newStack.add(headNode);
        BFS(newStack);
    }

    /********************************************************
     * Purpose: Recursive Breadth First Search algorithm.
     *
     * Algorithm (Recursive):
     *      For all n nodes in stack:
     *          Save n
     *          For all children c in n:
     *              Add c to new stack
     *
     *      Call recursively on new stack.
     *
     * @param stack: Nodes to visit.
     */
    public void BFS(Vector<AiNode> stack){

        // Loop through all nodes in stack.
        for(AiNode node : stack){
            finalStack.add(node);

            // Add all children to temporary stack
            for(AiNode child : node.getChildren()){
                tempStack.add(child);
            }
        }

        // set stack to temporary stack.
        stack.clear();
        stack.addAll(tempStack);
        tempStack.clear();

        if(stack.size() == 0){
            return;
        }

        // Call recursively on new stack.
        BFS(stack);
    }

    /********************************************************
     * Non recursive Best First Search.
     *
     * Algorithm:
     *
     *      First set up stack to hold root (head) node.
     *
     *      Then while stack is not empty:
     *          Remove the max node from the stack.
     *          For all children of max node:
     *              Add child to stack, priority goes to deepest node in stack.
     *
     *          Save max node.
     */
    private void BestFirstSearch(){
        createTree();

        // Set up stacks for search.
        finalStack.clear();
        tempStack.clear();
        tempStack.add(headNode);

        // Loop until tempStack is empty.
        while(!tempStack.isEmpty()){

            // Save the max node from stack.
            AiNode currentNode = removeMax(tempStack);
            tempStack.remove(currentNode);

            // Loop through its children.
            for(AiNode node : currentNode.getChildren()){

                // Add child to stack in order based on depth in tree.
                addInOrder(tempStack, node);
            }

            // Save curent node.
            finalStack.add(currentNode);
        }
    }

    /********************************************************
     * Purpose: Adds a node in order of their depth, higher depth goes first.
     *
     * @param stack: Stack to add node to.
     * @param a_nodeToAdd: Node to add to stack
     */
    public void addInOrder(Vector<AiNode> stack, AiNode a_nodeToAdd){

        // Loop through all elements in stack, starting at highest depth.
        for(int i = 0; i < stack.size(); i++){

            // Add node to stack if it is greater than the current node.
            if(a_nodeToAdd.getDepth() > stack.elementAt(i).getDepth()){
                stack.add(i, a_nodeToAdd);
                return;
            }
        }

        // If not greater than any in stack, of if stack is empty, add it at the end.
        stack.add(a_nodeToAdd);
    }

    /********************************************************
     *
     * @param a_depthLimit: Set the depth limit, used for Branch and Bound search.
     */
    public void setDepthLimit(int a_depthLimit){
        depthLimit = a_depthLimit;
    }

    /********************************************************
     * Purpose: Implements Branch And Bound Search algorithm with a depthLimit.
     *
     * Algorithm (Non-Recursive):
     *
     *      First set up stack to hold root (head) node.
     *
     *      Then while stack is not empty:
     *          Remove the max node from the stack.
     *
     *          For all children of max node:
     *              Add child to stack.
     *
     *          Save max node.
     */
    private void BranchAndBound(){
        // Create tree with depth limit.
        createTree(depthLimit);

        // Set up stacks.
        finalStack.clear();
        tempStack.clear();

        // Add root node to stack.
        tempStack.add(headNode);

        // Loop until tempStack is empty.
        while(!tempStack.isEmpty()){

            // Remove max from stack.
            AiNode currentNode = removeMax(tempStack);
            tempStack.remove(currentNode);

            // Add all of max's children to stack.
            for(AiNode node : currentNode.getChildren()){
                tempStack.add(node);
            }

            // Save current node.
            finalStack.add(currentNode);
        }
    }

    /********************************************************
     * Purpose: Removes the deepest node from a stack.
     *
     * @param stack: Stack to remove the max from.
     * @return: Node with largest depth value.
     */
    public AiNode removeMax(Vector<AiNode> stack){

        int currentHighest = -1;
        AiNode tempNode = null;

        // Loop through all nodes in stack.
        for(AiNode node : stack){

            // If current node's depth is greater than current highest, save it.
            if(currentHighest < node.getDepth()){
                currentHighest = node.getDepth();
                tempNode = node;
            }
        }

        return tempNode;
    }

    /********************************************************
     * Purpose: Gets the next move of the given search algorithm.
     *
     * @return: Next move in search.
     */
    public AiNode getNextMove(){

        // Check stack to make sure it is set correctly.
        checkStack();

        // Check if already went through all moves.
        if(locationInStack >= finalStack.size()){

            // If so, set location back to the beginning (Skipping head node).
            locationInStack = 1;
            return null;
        }

        // Return move.
        return finalStack.elementAt(locationInStack++);
    }

    /********************************************************
     * Purpose: Finds the first best move of a given search algorithm.
     *
     * @return: Best move in search algorithm.
     */
    public AiNode getBestMove(){

        // Check stack to make sure it is set correctly.
        checkStack();

        // Reset location in stack.
        locationInStack = 1;

        // Current highest score
        int highScore = 0;
        AiNode retNode = null;

        // Make sure stack is set up correctly.
        checkStack();

        // Loop through all nodes of a given search algorithm.
        for(AiNode node : finalStack){

            // Check if current node has greater depth than current highest score.
            if(node.getDepth() > highScore){

                // If is greater, save current node.
                highScore = node.getDepth();
                retNode = node;
            }
        }

        // Return node with highest depth.
        return retNode;
    }

    /********************************************************
     * Purpose: Checks stack to make sure it is set up correctly.
     * It does this by checking its length. At a minimum there should
     * be one value, the root node. If length is less than one then
     * create the tree.
     */
    public void checkStack(){

        // Check length of stack.
        if(finalStack.size() <= 1){

            // Create tree depending on which algorithm is slected.
            if(algorithm == 4){
                createTree(depthLimit);
            }else{
                createTree();
            }

            search();
        }
    }


    /********************************************************
     *
     * Purpose: Calls the minimax function with alpha beta pruning.
     *
     * @param plyCutoff:    The number of plys for minimax to run through.
     * @return:     The best move the player should make.
     */
    public AiNode minMaxPruning(int plyCutoff){
        isPruning = true;
        //AiNode ret = minMax(plyCutoff);

        count = 0;
        headNode = new AiNode(game);
        AiNode ret = miniMaxPruning(headNode, plyCutoff, true, headNode.game.isComputerTurn(), Integer.MIN_VALUE, Integer.MAX_VALUE);

        isPruning = false;
        return ret;
    }


    /********************************************************
     *
     * Purpose: Calls the minimax function with out alpha beta pruning.
     *
     * @param plyCutoff: Number of plys for minimax to run through.
     * @return
     */
    public AiNode minMax(int plyCutoff){
        count = 0;
        headNode = new AiNode(game);
        //AiNode ret = miniMax(headNode, plyCutoff, true, headNode.game.isComputerTurn(), Integer.MIN_VALUE, Integer.MAX_VALUE);


        AiNode ret = miniMax(headNode, plyCutoff, true, headNode.game.isComputerTurn());



        Log.e("count:", Integer.toString(count));
        int points = findPoints(ret, true);
        Log.e("Human", "Points: " + points);

        points = findPoints(ret, false);
        Log.e("Computer", "Points: " + points);
        return ret;
    }

    public int findPoints(AiNode node, boolean humanPoints){

        AiNode child = node;

        while(child != null){
            node = child;
            child = node.getChildAt(0);
        }

        if(humanPoints){
            return node.game.getHumanScore();
        }else{
            return node.game.getComputerScore();
        }
    }


    /********************************************************
     *
     * Purpose: This is the minimax function. It finds the best move based on a heuristic
     *  score calculated by player score - opponent score.
     *
     * @param node:     Current node in tree to be examined.
     * @param depth:    Current minimax number of plys to search through.
     * @param isMaximizing:     If minimax is currently looking for a max heuristic value.
     * @param isComputerTurn:   If it is currently the computers turn, this is used for switching
     *                      players after each move.
     * @return:                 Best current move to make, along with all opponents moves
     *                      as children.
     */
    public AiNode miniMax(AiNode node, int depth, boolean isMaximizing, boolean isComputerTurn){

        if(depth == 0){
            return node;
        }

        //tree(node, -1, 1);
        buildPly(node, isComputerTurn);
        Vector<AiNode> layerOne = new Vector<>();
        DFS(node, layerOne);

        if(isMaximizing){

            // Find max child.
            int currentMax = Integer.MIN_VALUE;
            AiNode maxNode = node;
            AiNode currentNode;

            for(int i = 1; i < layerOne.size(); i++){
                count++;
                currentNode = layerOne.elementAt(i);

                AiNode copy = new AiNode(currentNode);
                // Get min child.
                AiNode tempNode = miniMax(copy, depth - 1, false, !isComputerTurn);

                // If child is bigger, save current node.
                int score = tempNode.getHeuristicScore(headNode.game.getCurrentPlayerId());
                if(score > currentMax){
                    currentMax = score;
                    maxNode = currentNode;

                    // Add node as a child of the current node.
                    maxNode.removeChildren();
                    maxNode.addChild(tempNode);
                }
            }

            return maxNode;

        }else{
            // Find min child.
            int currentMin = Integer.MAX_VALUE;
            AiNode minNode = node;
            AiNode currentNode;

            for(int i = 1; i < layerOne.size(); i++){
                count++;
                currentNode = layerOne.elementAt(i);

                AiNode copy = new AiNode(currentNode);
                // Get max child.
                AiNode tempNode = miniMax(copy, depth - 1, true, !isComputerTurn);

                // If child is smaller, save current node.
                int score = tempNode.getHeuristicScore(headNode.game.getCurrentPlayerId());
                if(score < currentMin){
                    currentMin = score;
                    minNode = currentNode;

                    // Add node as a child of the current node.
                    minNode.removeChildren();
                    minNode.addChild(tempNode);
                }
            }
            return minNode;
        }
    }



    /********************************************************
     *
     * Purpose: This is the minimax function. It finds the best move based on a heuristic
     *  score calculated by player score - opponent score.
     *
     * @param node:     Current node in tree to be examined.
     * @param depth:    Current minimax number of plys to search through.
     * @param isMaximizing:     If minimax is currently looking for a max heuristic value.
     * @param isComputerTurn:   If it is currently the computers turn, this is used for switching
     *                      players after each move.
     * @param alpha:            Current alpha for alpha beta pruning.
     * @param beta:             Current beta for alpha beta pruning.
     * @return:                 Best current move to make, along with all opponents moves
     *                      as children.
     */
    public AiNode miniMaxPruning(AiNode node, int depth, boolean isMaximizing, boolean isComputerTurn, int alpha, int beta){

        if(depth == 0){
            return node;
        }

        //tree(node, -1, 1);
        buildPly(node, isComputerTurn);
        Vector<AiNode> layerOne = new Vector<>();
        DFS(node, layerOne);

        if(isMaximizing){

            // Find max child.
            int currentMax = Integer.MIN_VALUE;
            AiNode maxNode = node;
            AiNode currentNode;

            for(int i = 1; i < layerOne.size(); i++){
                count++;
                currentNode = layerOne.elementAt(i);

                AiNode copy = new AiNode(currentNode);
                // Get min child.
                AiNode tempNode = miniMaxPruning(copy, depth - 1, false, !isComputerTurn, alpha, beta);

                // If child is bigger, save current node.
                int score = tempNode.getHeuristicScore(headNode.game.getCurrentPlayerId());
                if(score > currentMax){
                    currentMax = score;
                    maxNode = currentNode;

                    // Add node as a child of the current node.
                    maxNode.removeChildren();
                    maxNode.addChild(tempNode);
                }

                if(isPruning){

                    if(currentMax > alpha){
                        alpha = currentMax;
                    }

                    if(beta <= alpha){
                        return maxNode;
                    }
                }
            }

            return maxNode;

        }else{
            // Find min child.
            int currentMin = Integer.MAX_VALUE;
            AiNode minNode = node;
            AiNode currentNode;

            for(int i = 1; i < layerOne.size(); i++){
                count++;
                currentNode = layerOne.elementAt(i);

                AiNode copy = new AiNode(currentNode);
                // Get max child.
                AiNode tempNode = miniMaxPruning(copy, depth - 1, true, !isComputerTurn, alpha, beta);

                // If child is smaller, save current node.
                int score = tempNode.getHeuristicScore(headNode.game.getCurrentPlayerId());
                if(score < currentMin){
                    currentMin = score;
                    minNode = currentNode;

                    // Add node as a child of the current node.
                    minNode.removeChildren();
                    minNode.addChild(tempNode);
                }

                if(isPruning){
                    if(currentMin < beta){
                        beta = currentMin;
                    }

                    if(beta <= alpha){
                        return minNode;
                    }
                }
            }
            return minNode;
        }
    }




    /**
     *
     * @return:     Number of nodes visited by minimax.
     */
    public int getNodesVisited(){
        return count;
    }


    /**
     *
     * Purpose: Builds one ply for minimax. It moves the current player to the next player
     *      for each new ply.
     *
     * @param node:             Current node in minimax.
     * @param isComputerTurn:   If it is currently the computers turn.
     */
    public void buildPly(AiNode node, boolean isComputerTurn){

        // Check if current player is the desired player. If not, switch players.
        if(node.game.isComputerTurn() != isComputerTurn){
            node.game.moveToNextPlayer();
        }

        // Create tree for this ply.
        tree(node, -1, 1);
    }

}

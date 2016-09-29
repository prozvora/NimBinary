import java.io.IOException;
import java.util.ArrayList;
/**
 * NimModel is the model for each game session. It handles the game logic for
 * Nim.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class NimModel implements ViewListener {
    SessionManager manager;
    private ArrayList<ModelListener> listenerList = new ArrayList<ModelListener>();
    private int[] heap = {3, 4, 5};
    private int[] score = {0, 0};
    private int turn = 0;
    private String[] names = {"", ""};
    /**
     * Adds a model listener for each player who joins
     * @param modelListener The model listener that messages will go through
     */
    public void addModelListener(ModelListener modelListener) {
        listenerList.add(modelListener);
    }
    /**
     * The join method sends the players name and a model listener to the
     * server in order to be placed in a game session.
     * @param proxy A reference to the view proxy object for the client
     * @param name  The player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void join(NimViewProxy proxy, String name) throws IOException {
        if (listenerList.size() == 1) {
            names[0] = name;
            listenerList.get(0).id(0);
            listenerList.get(0).name(0, name);
            listenerList.get(0).score(0, 0);
        } else {
            names[1] = name;
            listenerList.get(1).id(1);
            sendHeapStatus();
            sendNames();
            sendScore();
            sendTurn();
        }
    }
    /**
     * This method tells the server what move the player made.
     * @param heapId    id of heap from which markers were taken
     * @param markers   how many markers were taken
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void take(int heapId, int markers) throws IOException {
        heap[heapId] -= markers;
        if (heap[0] == 0 && heap[1] == 0 && heap[2] == 0) {
            sendWin();
            score[turn] += 1;
            sendScore();
            sendHeapStatus();
            turn = 0;
        } else {
            turn++;
            turn = turn % 2;
            sendHeapStatus();
            sendTurn();
        }
    }
    /**
     * This method informs the server that a player would like to start a new
     * game.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void newGame() throws IOException {
        heap[0] = 3;
        heap[1] = 4;
        heap[2] = 5;
        turn = 0;
        sendHeapStatus();
        sendTurn();
        
    }
    /**
     * This method informs the server that a player has terminated the program.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException {
        sendQuit();
    }
    /**
     * This method communicates the heap status to the players.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendHeapStatus() throws IOException {
        for (int i = 0; i <= 2; i++) {
            listenerList.get(0).heap(i, heap[i]);
            listenerList.get(1).heap(i, heap[i]);
        }
    }
    /**
     * This method communicates whose turn it is to the players.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendTurn() throws IOException {
        listenerList.get(0).turn(turn);
        listenerList.get(1).turn(turn);
    }
    /**
     * This method communicates the winner to the players.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendWin() throws IOException {
        listenerList.get(0).win(turn);
        listenerList.get(1).win(turn);
    }
    /**
     * This method communicates the score to the players.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendScore() throws IOException {
        listenerList.get(0).score(turn, score[turn]);
        listenerList.get(1).score(turn, score[turn]);
    }
    /**
     * This method communicates the names of both players to the players.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendNames() throws IOException {
        listenerList.get(0).name(0, names[0]);
        listenerList.get(0).name(1, names[1]);
        listenerList.get(1).name(0, names[0]);
        listenerList.get(1).name(1, names[1]);
    }
    /**
     * This method communicates a quit action to the players or the session
     * manager.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    private void sendQuit() throws IOException {
        if (listenerList.size() == 2) {
            listenerList.get(0).quit();
            listenerList.get(1).quit();
        } else {
        // the manager only resets the current session if the session isn't full
            manager.quit();
        }
    }
}

import java.io.IOException;
/**
 * The ModelListener interface lays out the methods needed for communication
 * from server to client.
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-11-06
 *
 */
public interface ModelListener {
    /**
     * This method sets the player's id and initializes the window.
     * @param id    the player's id
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void id(int id) throws IOException;
    /**
     * This method sets the player names. Scores initialized to 0.
     * @param id    the player to whom this name belongs
     * @param name  the player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void name(int id, String name) throws IOException;
    /**
     * This method sets the player scores.
     * @param id    the player to whom this score belongs
     * @param score the player's score
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void score(int id, int score) throws IOException;
    /**
     * This method updates the state of a heap.
     * @param heapId    the id of the heap
     * @param markers   the amount of markers in the heap
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void heap(int heapId, int markers) throws IOException;
    /**
     * This method tells the players whose turn it is. The heap is disabled
     * if the other player is taking their turn.
     * @param id    id of the player who is taking their turn
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void turn(int id) throws IOException;
    /**
     * This method informs the players of the game's winner.
     * @param id    id belonging to the winner
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void win(int id) throws IOException;
    /**
     * This method informs the player that the other player quit by closing
     * the window.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException;
}

import java.io.IOException;
/**
 * The ViewListener interface lays out the methods needed for communication
 * from client to server.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public interface ViewListener {
    /**
     * The join method sends the players name and a model listener to the
     * server in order to be placed in a game session.
     * @param proxy A reference to the view proxy object for the client
     * @param name  The player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void join(NimViewProxy proxy, String name) throws IOException;
    /**
     * This method tells the server what move the player made.
     * @param heapId    id of heap from which markers were taken
     * @param markers   how many markers were taken
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void take(int heapId, int markers) throws IOException;
    /**
     * This method informs the server that a player would like to start a new
     * game.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void newGame() throws IOException;
    /**
     * This method informs the server that a player has terminated the program.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException;
}
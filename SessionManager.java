import java.io.IOException;
/**
 * SessionManager handles the matchmaking for the server. It places a player
 * into the available space in an existing session, or it creates a new session
 * if there is no available space in an existing session.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class SessionManager implements ViewListener {    
    private NimModel currentSession;
    /**
     * Constructor.
     */
    public SessionManager() {}
    /**
     * The join method sends the players name and a model listener to the
     * server in order to be placed in a game session.
     * @param proxy A reference to the view proxy object for the client
     * @param name  The player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void join(NimViewProxy proxy, String name) throws IOException {
        if (currentSession == null) {
            currentSession = new NimModel();
            currentSession.manager = this;
            currentSession.addModelListener(proxy);
            proxy.setViewListener(currentSession);
            currentSession.join(proxy, name);
        } else { // a player is waiting in the current session
            currentSession.addModelListener(proxy);
            proxy.setViewListener(currentSession);
            currentSession.join(proxy, name);
            // session full, the next player to join will need a new session
            currentSession = null;
        }
    }
    /**
     * This method tells the server what move the player made.
     * @param heapId    id of heap from which markers were taken
     * @param markers   how many markers were taken
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void take(int heapId, int markers) throws IOException {}
    /**
     * This method informs the server that a player would like to start a new
     * game.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void newGame() throws IOException {}
    /**
     * This method informs the server that a player has terminated the program.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException {
        currentSession = null;
    }
}

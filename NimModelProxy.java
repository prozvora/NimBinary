import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
/**
 * The NimModelProxy class manages the connection between the server and client
 * and relays messages between them.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-11-08
 */
public class NimModelProxy implements ViewListener {
    private DatagramSocket mailbox;
    private SocketAddress dest;
    private ModelListener modelListener;
    /**
     * Constructor.
     * @param socket    the socket over which the server and client communicate
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public NimModelProxy(DatagramSocket mailbox, SocketAddress dest) 
            throws IOException {
        this.mailbox = mailbox;
        this.dest = dest;
    }
    /**
     * Sets the ModelProxy's model listener
     * @param modelListener The model listener that messages will go through
     */
    public void setModelListener(ModelListener modelListener) {
        this.modelListener = modelListener;
        new ReaderThread() .start();
    }
    // implement ViewListener
    /**
     * The join method sends the players name and a model listener to the
     * server in order to be placed in a game session.
     * @param proxy A reference to the view proxy object for the client
     * @param name  The player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void join(NimViewProxy proxy, String name) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('J');
        out.writeUTF(name);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, dest));
    }
    /**
     * This method tells the server what move the player made.
     * @param heapId    id of heap from which markers were taken
     * @param markers   how many markers were taken
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void take(int heapId, int markers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('T');
        out.writeByte(heapId);
        out.writeByte(markers);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, dest));
    }
    /**
     * This method informs the server that a player would like to start a new
     * game.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void newGame() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('N');
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, dest));
    }
    /**
     * This method informs the server that a player has terminated the program.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('Q');
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, dest));
    }
    /**
     * ReaderThread is a thread which handles messages from the server.
     * @author  Pavel Rozvora (pxr8306)
     * @version 2015-11-06
     */
    private class ReaderThread extends Thread {
        /**
         * Listens for messages and calls the corresponding methods according to
         * the protocol.
         */
        public void run() {
            byte[] message = new byte[128];
            try {
                while(true) {
                    DatagramPacket packet =
                            new DatagramPacket (message, message.length);
                    mailbox.receive(packet);
                    DataInputStream in = new DataInputStream(
                            new ByteArrayInputStream(
                                    message, 0, packet.getLength()));
                    int id;
                    byte b = in.readByte();
                    switch (b) {
                    case 'I':
                        id = in.readByte();
                        modelListener.id(id);
                        break;
                    case 'N':
                        id = in.readByte();
                        String name = in.readUTF();
                        modelListener.name(id, name);
                        break;
                    case 'S':
                        id = in.readByte();
                        int score = in.readByte();
                        modelListener.score(id, score);
                        break;
                    case 'H':
                        int heapId = in.readByte();
                        int markers = in.readByte();
                        modelListener.heap(heapId, markers);
                        break;
                    case 'T':
                        id = in.readByte();
                        modelListener.turn(id);
                        break;
                    case 'W':
                        id = in.readByte();
                        modelListener.win(id);
                        break;
                    case 'Q':
                        modelListener.quit();
                        break;
                    default:
                        System.err.println("Bad message");
                        break;
                    }
                }
            } catch (IOException exc){}
            finally {
                mailbox.close();
            }
        }
    }
}

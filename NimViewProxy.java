import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
/**
 * The NimViewProxy class manages the connection between the server and client
 * and relays messages between them.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class NimViewProxy implements ModelListener {
    private DatagramSocket mailbox;
    private SocketAddress clientAddress;
    private ViewListener viewListener;
    /**
     * Constructor
     * @param mailbox    The mailbox where messages are sent through
     * @param clientAddress The client's address, the destination for messages
     *                      from the model
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public NimViewProxy(DatagramSocket mailbox, SocketAddress clientAddress) {
        this.mailbox = mailbox;
        this.clientAddress = clientAddress;
    }
    /**
     * Sets the ViewProxy's viewListener
     * @param viewListener The view listener that messages will go through
     */
    public void setViewListener(ViewListener viewListener) {
        this.viewListener = viewListener;
    }
    // implement ModelListener
    /**
     * This method sets the player's id and initializes the window.
     * @param id    the player's id
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void id(int id) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('I');
        out.writeByte(id);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method sets the player names. Scores initialized to 0.
     * @param id    the player to whom this name belongs
     * @param name  the player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void name(int id, String name) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('N');
        out.writeByte(id);
        out.writeUTF(name);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method sets the player scores.
     * @param id    the player to whom this score belongs
     * @param score the player's score
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void score(int id, int score) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('S');
        out.writeByte(id);
        out.writeByte(score);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method updates the state of a heap.
     * @param heapId    the id of the heap
     * @param markers   the amount of markers in the heap
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void heap(int heapId, int markers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('H');
        out.writeByte(heapId);
        out.writeByte(markers);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method tells the players whose turn it is. The heap is disabled
     * if the other player is taking their turn.
     * @param id    id of the player who is taking their turn
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void turn(int id) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('T');
        out.writeByte(id);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method informs the players of the game's winner.
     * @param id    id belonging to the winner
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void win(int id) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('W');
        out.writeByte(id);
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * This method informs the player that the other player quit by closing
     * the window.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void quit() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte('Q');
        out.close();
        byte[] message = baos.toByteArray();
        mailbox.send(new DatagramPacket(message, message.length, clientAddress));
    }
    /**
     * The process method receives a datagram and updates the model with actions
     * corresponding to the received message.
     * @param datagram  The packet of information from the view
     * @return  true if this view can be subsequently ignored, otherwise false
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public boolean process(DatagramPacket datagram) throws IOException {
        boolean discard = false;
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                datagram.getData(), 0, datagram.getLength()));
        byte b = in.readByte();
        switch (b) {
        case 'J':
            String name = in.readUTF();
            viewListener.join(NimViewProxy.this, name);
            break;
        case 'T':
            int heapId = in.readByte();
            int markers = in.readByte();
            viewListener.take(heapId, markers);
            break;
        case 'N':
            viewListener.newGame();
            break;
        case 'Q':
            discard = true;
            viewListener.quit();
            break;
        default:
            System.err.println("Bad message");
            break;
        }
        return discard;
    }
}

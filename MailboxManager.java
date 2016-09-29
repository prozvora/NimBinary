import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
/**
 * The MailboxManager class manages the mailbox for NimServer. It tracks all
 * relevant view proxies, reads incoming datagrams, and forwards datagrams to
 * the correct view proxy.
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class MailboxManager {
    private DatagramSocket mailbox;
    private HashMap<SocketAddress, NimViewProxy> proxyMap = 
            new HashMap<SocketAddress, NimViewProxy>();
    private byte[] message = new byte[128];
    private SessionManager sessionManager = new SessionManager();
    /**
     * Constructor. Constructs a mailbox manager.
     * @param mailbox  mailbox to read datagrams from 
     */
    public MailboxManager(DatagramSocket mailbox) {
        this.mailbox = mailbox;
    }
    /**
     * This method reads in a datagram and processes it.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public void receiveMessage() throws IOException {
        DatagramPacket packet = new DatagramPacket(message, message.length);
        mailbox.receive(packet);
        SocketAddress clientAddress = packet.getSocketAddress();
        NimViewProxy proxy = proxyMap.get(clientAddress);
        if (proxy == null) {
            proxy = new NimViewProxy(mailbox, clientAddress);
            proxy.setViewListener(sessionManager);
            proxyMap.put(clientAddress, proxy);
        }
        if (proxy.process(packet)) {
            proxyMap.remove(clientAddress);
        }
    }
}

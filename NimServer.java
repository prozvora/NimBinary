import java.net.InetSocketAddress;
import java.net.DatagramSocket;
/**
 * NimServer is the server's main program. It starts up the model and server
 * and allows clients to connect.
 * Usage: java NimServer <I>serverhost</I> <I>serverport</I>
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class NimServer {
    /**
     * Main program. Starts the server and waits for connections.
     * @param args          Command line arguments. Specified by usage above.
     * @throws Exception    Thrown if initialization fails at some point
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage();
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        DatagramSocket mailbox = new DatagramSocket(
                new InetSocketAddress(host, port));
        MailboxManager manager = new MailboxManager(mailbox);
        while(true) {
            manager.receiveMessage();
        }
    }
    /**
     * The usage method checks to make sure the program's arguments are
     * supplied correctly.
     */
    private static void usage() {
        System.err.println("Usage: java NimServer <serverhost> <serverport>");
        System.err.println("<serverhost> is the server's host name or IP address.");
        System.err.println("<serverport> is the port number of the server.");
        System.exit(0);
    }
}

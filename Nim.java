import java.net.DatagramSocket;
import java.net.InetSocketAddress;
/**
 * Nim is the client's main program. It starts the UI and creates a connection
 * from client to server.
 * Usage: java Nim <I>serverhost</I> <I>serverport</I> <I>clienthost</I>
 * <I>clientport</I> <I>playername</I>
 * 
 * @author  Pavel Rozvora (pxr8306)
 * @version 2015-12-08
 */
public class Nim {
    /**
     * Main program. Initializes the Nim client and connection to the server.
     * @param args          Command line arguments. Specified by usage above.
     * @throws Exception    Thrown if initialization fails at some point
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            usage();
        }
        String serverhost = args[0];
        int serverport = Integer.parseInt(args[1]);
        String host = args[2];
        int port = Integer.parseInt(args[3]);
        String playername = args[4];
        
        DatagramSocket mailbox = new DatagramSocket(
                new InetSocketAddress(host, port));
        NimUI view = NimUI.create(playername);
        final NimModelProxy proxy = new NimModelProxy(
                mailbox, new InetSocketAddress(serverhost, serverport));
        view.setViewListener(proxy);
        proxy.setModelListener(view);
        proxy.join(null, playername);
    }
    /**
     * The usage method checks to make sure the program's arguments are
     * supplied correctly.
     */
    private static void usage() {
        System.err.println("Usage: java Nim <serverhost> <serverport> "
                + "<clienthost> <clientport> <playername>");
        System.err.println("<serverhost> is the server's host name or IP address.");
        System.err.println("<serverport> is the port number of the server.");
        System.err.println("<clienthost> is the client's host name or IP address.");
        System.err.println("<clientport> is the port number of the client.");
        System.err.println("<playername> is the player's name. It may not "
                + "contain whitespace.");
        System.exit(0);
    }
}

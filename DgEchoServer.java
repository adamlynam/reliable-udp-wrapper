import java.net.*;

/**
 * DgEchoServer. <p>
 *
 * echo datagrams - user can set port from cmd line, or get free one from sys
 * this version changes the first char to 'y' before returning packet. <p>
 *
 * @author Lloyd, Jed, Wayne and Mark
 * @version $Revision: 1.0 $
 */
class DgEchoServer {

  /**
   * Main method for this class. 
   *
   * @param args command line arguments. -p <port>
   *
   */
  public static void main(String[] args) {
    int		   port = 0;
    int		   len;
    InetAddress    address;
    ReliableDatagramSocket socket = null;
    DatagramPacket packet;
    byte[]	   buf = new byte[256];
    int		   i = 0;
    String	   arg;

    while (i < args.length) {
      arg = args[i++];

      if (arg.equals("-p")) {
	port = Integer.parseInt(args[i++]);
      } 
    } 

    arg = null;    // let garbage collector have it

    try {  // if port not given on cmd line, let system assign it
      if (port == 0) {
	socket = new ReliableDatagramSocket();    // system assigns port

	System.out.println("Listening on port " + socket.getLocalPort());
      } else {
	socket = new ReliableDatagramSocket(port);
      }
    } catch (Exception e) {
      System.out.println("Exception " + e);
      System.exit(1);
    } 

    while (true) {
      try {
	packet = new DatagramPacket(buf, 256);

	socket.receive(packet);

	address = packet.getAddress();    // get return address
	port = packet.getPort();
	len = packet.getLength();
	buf[0] = (byte) 'y';
	packet = new DatagramPacket(buf, len, address, port);

	socket.send(packet);
      } catch (Exception e) {
	System.out.println("Exception " + e);
	System.exit(1);
      } 
    } 
  } 
}





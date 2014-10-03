import java.net.*;
import java.io.*;

/**
 * DgEchoClient. <p>
 *
 * Send datagrams to echo server. <p>
 *
 * @author Lloyd, Jed, Wayne and Mark
 * @version $Revision: 1.0 $
 */
class DgEchoClient {

  /**
   * Main method for this class
   *
   * @param args holds command line arguments. -h host -p port
   *
   */
  public static void main(String[] args) {
    int		   port = 0;
    InetAddress    address = null;
    ReliableDatagramSocket socket = null;
    DatagramPacket packet;
    byte[]	   buf = new byte[256];
    int		   i = 0;
    String	   arg;

    while (i < args.length) {
      arg = args[i++];

      if (arg.equals("-p")) {
	port = Integer.parseInt(args[i++]);
      } else if (arg.equals("-h")) {
	try {    // convert host from name to ip address
	  address = InetAddress.getByName(args[i++]);
	} catch (UnknownHostException e) {
	  System.out.println("Host " + args[i - 1] + " unknown");
	  System.exit(1);
	} 
      } 
    } 

    if (address == null || port == 0) {
      System.out.println("Usage: java DgEchoClient -h <hostname> -p <port#>");
      System.exit(1);
    } 

    try {
      socket = new ReliableDatagramSocket();    // let system assign local port
    } catch (Exception e) {
      System.out.println("Unable to create socket");
      System.exit(1);
    } 

    int c;

    i = 0;

    String msg;

    try {
      while ((c = System.in.read()) != -1) {
	if (c != '\n') {
	  buf[i++] = (byte) c;    // put chars into packet buffer
	} else if (i > 0) {       // send string if it's not null
	  packet = new DatagramPacket(buf, i, address, port);
	  i = 0;		  // get index ready for next msg

	  try {
	    socket.send(packet);
	  } catch (Exception e) {
	    System.out.println("Exception " + e);
	    System.exit(1);
	  } 

	  packet = new DatagramPacket(buf, 256);

	  try {
	    socket.receive(packet);
	  } catch (Exception e) {
	    System.out.println("Exception " + e);
	    System.exit(1);
	  } 

	  msg = new String(packet.getData(), 0, packet.getLength());
	  //	  msg = new String(packet.getData(), 0);
	  //	  msg = msg.substring(0, packet.getLength());

	  System.out.println(msg);
	} 
      } 
    } catch (IOException e) {
      System.out.println("IOException " + e);
      System.exit(1);
    } 
  } 
}





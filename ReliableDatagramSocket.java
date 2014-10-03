// class ReliableDatagramSocket:
//
// Sample template for 0657.312b Assignment I
// Implements a reliable datagram service over UDP

import java.util.zip.*;
import java.io.*;
import java.net.*;

// We extend DodgyDatagramSocket, which in turn extends DatagramSocket
// This replacement is transparent to you in terms of methods you can
// call on the socket, except that of course you should be filling in
// this skeleton with code to implement a reliable transport method
// over the "dodgy" transport provided by DodgyDatagramSocket.

class ReliableDatagramSocket extends DodgyDatagramSocket {

	int sequenceNumberSend;
	int sequenceNumberReceive;

  // Call inherited constructors
  public ReliableDatagramSocket() throws SocketException  {
    super();
	sequenceNumberSend = 0;
	sequenceNumberReceive = 0;
  }

  
  public ReliableDatagramSocket( int port ) throws SocketException  {
    super( port );
	sequenceNumberSend = 0;
	sequenceNumberReceive = 0;
  }

  // Override send method to append RDP headers/trailers etc
  public void send(DatagramPacket p) throws IOException {
    // Do stuff here
      
      CRC32 checksum = new CRC32();
      checksum.update(p.getData(), 0, p.getLength());
      
      /*TESTING
		System.err.println(new String(p.getData(), 0, p.getLength()));
		System.err.println(p.getLength());
      System.err.println(checksum.getValue());
*/

		String headerString = String.valueOf(checksum.getValue()) + "*" + sequenceNumberSend + "&";
		sequenceNumberSend++;
		String dataString = new String(p.getData(), 0, p.getLength());

		byte[] addHeader = new byte[dataString.length() + headerString.length()];

		int index = 0;
		for(int i = 0; i < headerString.length(); i++)
		{
			addHeader[index] = (byte)headerString.charAt(i);
			index++;
		}
		for(int i = 0; i < dataString.length(); i++)
		{
			addHeader[index] = (byte)dataString.charAt(i);
			index++;
		}

		DatagramPacket myPacket = new DatagramPacket(addHeader, addHeader.length, p.getAddress(), p.getPort());
            
        byte[] ackData = new byte[512];
        DatagramPacket acknowledgement = new DatagramPacket(ackData, 512);
        boolean notReceived = true;
        while (notReceived)
        {
            super.send( myPacket ); // do not delete this line!!

            super.setSoTimeout(3500);   
            try
            {
                super.receive(acknowledgement);
				String ack = new String(acknowledgement.getData(), 0, acknowledgement.getLength());
				//DEBUG System.err.println(ack);
                if (ack.startsWith("acknowledged"))
                {
					notReceived = false;
                }
            }
            catch (Exception e)
            {
                System.err.println("Timeout, resending");
                notReceived = true;
            }
        }
  }

  // Override receive method 
  public void receive(DatagramPacket p) throws IOException {

		DatagramPacket myPacket = null;

      boolean receivedProperly = false;
      while(!receivedProperly)
      {
			byte[] myData = new byte[1024];
			myPacket = new DatagramPacket(myData, 1024);

          super.setSoTimeout(0);
          super.receive( myPacket ); // do not delete this line!! 

        // Do stuff here
 			String data = new String(myPacket.getData(), 0, myPacket.getLength());

          	int checksumEnd = data.indexOf('*');
			int headerEnd = data.indexOf('&');

			//this is to break the checksum if the header is corrupted
			if (checksumEnd < 0 || headerEnd < 0)
			{
				checksumEnd = 1;
				headerEnd = 1;
			}

			String myChecksum = data.substring(0, checksumEnd);	
			String packetSequenceNumber = data.substring(checksumEnd + 1, headerEnd);
          		String realData = data.substring(headerEnd + 1);

          /*TESTING
			System.err.println(myHeader);
          System.err.println(realData);
			*/

          byte[] toFill = p.getData();
          for(int i = 0; i < realData.length(); i++)
          {
              toFill[i] = (byte)realData.charAt(i);
          }
          p.setLength(realData.length());

          CRC32 checksum = new CRC32();
          checksum.update(p.getData(), 0, p.getLength());

          /*TESTING
			System.err.println(new String(p.getData(), 0, p.getLength()));
			System.err.println(p.getLength());
          System.err.println(checksum.getValue());
          System.err.println(Long.parseLong(myHeader));
			*/

          try
          {
			/* DEBUG
			System.err.println(sequenceNumberReceive);
			System.err.println(Integer.parseInt(packetSequenceNumber));
			*/
            if(checksum.getValue() != Long.parseLong(myChecksum))
            {
                System.err.println("Checksum failed!");

                receivedProperly = false;
            }			
			else if (sequenceNumberReceive != Integer.parseInt(packetSequenceNumber))
			{
				System.err.println("Sequence incorrect, resending ack");
				String ack = new String("acknowledged");
      			DatagramPacket acknowledgement = new DatagramPacket(ack.getBytes(), ack.length(), myPacket.getAddress(), myPacket.getPort());
				super.send(acknowledgement);
				receivedProperly = false;
			}
            else
            {
                receivedProperly = true;				
            }
          } catch (Exception e)
          {
                System.err.println("Problem with header!");
                receivedProperly = false;
          }
      }
      String ack = new String("acknowledged");
      DatagramPacket acknowledgement = new DatagramPacket(ack.getBytes(), ack.length(), myPacket.getAddress(), myPacket.getPort());
		sequenceNumberReceive++;
      super.send(acknowledgement);
  }
}






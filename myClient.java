/*
 * myClient.java
 *
 * Created on 5 May 2005, 01:13
 */

import java.net.*;
import java.io.*;

/**
 *
 * @author Adam Lynam
 *  ID # 1000026
 */
public class myClient {
    
    /** Creates a new instance of myClient */
    public myClient()
    {
        
    }
    
    public static void main(String args[])
    {
        InetAddress serverAddress = null;
        int portToConnectOn  = 0;
        String filename = null;
        
        try
        {
            serverAddress = InetAddress.getByName(args[0]);
            portToConnectOn = Integer.parseInt(args[1]);
            filename = args[2];
        }
        catch (Exception e)
        {
            System.out.println("Please start the client using the format \"myclient <server name> <port number> <filename>\"");
            System.exit(1);
        }
        
        
        ReliableDatagramSocket socket = null;
        
        try
        {
            socket = new ReliableDatagramSocket();
        }
        catch (Exception e)
        {
            System.out.println("Failed to prepare a socket for communication");
            System.exit(1);
        }
            
        
        FileInputStream fileReader = null;
        
        try
        {
            fileReader = new FileInputStream(filename);

            boolean moreToSend = true;
            while(moreToSend)
            {
                byte[] toSend = new byte[512];
                int bytesRead = fileReader.read(toSend);
                
                //DEBUG
                System.err.println(new String (toSend, 0, toSend.length));
                
                if(bytesRead == -1)
                {
                    moreToSend = false;
                }
                else
                {
                    DatagramPacket newPacket = new DatagramPacket(toSend, bytesRead, serverAddress, portToConnectOn);

                    socket.send(newPacket);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Transfer failed");
            System.out.println("Exception " + e);
            System.exit(1);
        }
        
        try
        {
            fileReader.close();
        }
        catch (Exception e)
        {
            System.out.println("File could not be closed");
            System.exit(1);
        }
    }
}

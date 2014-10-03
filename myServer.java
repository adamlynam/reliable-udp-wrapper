/*
 * myServer.java
 *
 * Created on 5 May 2005, 00:40
 */

import java.net.*;
import java.io.*;

/**
 *
 * @author Adam Lynam
 * ID # 1000026
 */
public class myServer {
    
    /** Creates a new instance of myServer */
    public myServer()
    {
        
    }
    
    public static void main(String args[])
    {
        int portToListenOn  = 0;
        
        try
        {
            portToListenOn = Integer.parseInt(args[0]);
        }
        catch (Exception e)
        {
            System.out.println("Please start the server using the format \"myserver <portnumber>\"");
            System.exit(1);
        }
        
        ReliableDatagramSocket socket = null;
        
        try
        {
            socket = new ReliableDatagramSocket(portToListenOn);
        }
        catch (Exception e)
        {
            System.out.println("Failed to listen on port " + portToListenOn);
            System.exit(1);
        }
        
        
        FileOutputStream fileWriter = null;
        
        try
        {
            fileWriter = new FileOutputStream("outputFile.txt");
        }
        catch (Exception e)
        {
            System.out.println("Failed to create stream for new file");
            System.exit(1);
        }
        
        boolean receiving = true;
        while(receiving)
        {
            DatagramPacket newPacket = null;
            
            try
            {
                byte[] packetData = new byte[1024];
                newPacket = new DatagramPacket(packetData, 1024);

                socket.receive(newPacket);
            }
            catch (Exception e)
            {
                System.out.println("Transfer failed");
                System.out.println("Exception " + e);
                System.exit(1);
            }
            
            try
            {
                //DEBUG
                //System.err.println("Writing : " + new String(newPacket.getData(), 0, newPacket.getLength()));
                
                fileWriter.write(newPacket.getData(), 0, newPacket.getLength());
                fileWriter.flush();
            }
            catch (Exception e)
            {
                System.out.println("Transfer failed");
                System.out.println("Exception " + e);
                System.exit(1);
            }
        }
        
        try
        {
            fileWriter.close();
        }
        catch (Exception e)
        {
            System.out.println("File could not be closed");
            System.exit(1);
        }
    }
}

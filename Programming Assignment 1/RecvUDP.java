import java.net.*;  // for DatagramSocket and DatagramPacket
import java.io.*;   // for IOException
import java.nio.charset.StandardCharsets;

public class RecvUDP {

  public static void main(String[] args) throws Exception {

      if (args.length != 1 && args.length != 2)  // Test for correct # of args        
	  throw new IllegalArgumentException("Parameter(s): <Port> [<encoding>]");
      
      int port = Integer.parseInt(args[0]);   // Receiving Port
      
      DatagramSocket sock = new DatagramSocket(port);  // UDP socket for receiving      
      DatagramPacket packet = new DatagramPacket(new byte[1024],1024);
      while(true){
        sock.receive(packet);
      

      // Display received bytes in hexadecimal
      byte[] receivedBytes = packet.getData();
      System.out.print("Received bytes in hexadecimal: ");
      for (byte b : receivedBytes) {
          System.out.print(String.format("%02X ", b));
      }
      System.out.println();

      // Check if the received data length is equal to 2 bytes
      if (packet.getLength() != 2) {
        String response = "****";
        // Display the error response
        System.out.println("Error: " + response);
      }

      // Convert received bytes to short 
      short number = (short) (((receivedBytes[0] & 0xFF) << 8) | (receivedBytes[1] & 0xFF));
      // Display the received number 𝑁
      System.out.println("Received number N: " + number);

      // Display the client's IP address and port number
      System.out.println("Client IP Address: " + packet.getAddress());
      System.out.println("Client Port Number: " + packet.getPort());

      // Convert the received number N to string 𝑆 using UTF-16 encoding
      String responseString = String.valueOf(number);
      byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_16BE);

      // Display response bytes in hexadecimal
      System.out.print("Sending response bytes in hexadecimal: ");
      for (byte b : responseBytes) {
          System.out.print(String.format("%02X ", b));
      }
      System.out.println();

      // Send the response S back to the client
      DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length,
          packet.getAddress(), packet.getPort());
      sock.send(responsePacket);
      
    } 
  }
}

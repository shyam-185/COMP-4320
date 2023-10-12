import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SendUDP {

    public static void main(String args[]) throws Exception {

        if (args.length != 2 && args.length != 3) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Destination>" +
                    " <Port> [<encoding]");

        InetAddress destAddr = InetAddress.getByName(args[0]); // Destination address
        int destPort = Integer.parseInt(args[1]); // Destination port

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(); // UDP socket for sending

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                int number = getUserInput(userInput);

                // Convert the number to a byte array (2 bytes for a short integer)
                byte[] numberBytes = new byte[2];
                numberBytes[0] = (byte) ((number >> 8) & 0xFF); // Most significant byte
                numberBytes[1] = (byte) (number & 0xFF); // Least significant byte

                // Display byte per byte in hexadecimal
                System.out.print("Sending number N as hexadecimal: ");
                for (byte b : numberBytes) {
                    System.out.print(String.format("%02X ", b));
                }
                System.out.println();

                // Create UDP packet containing the number N
                DatagramPacket message = new DatagramPacket(numberBytes, numberBytes.length,
                        destAddr, destPort);

                socket.send(message); // Send the packet as a short integer after displaying N in hex form

                // Measure round-trip times for 5 different numbers
                measurements(socket, destAddr, destPort);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Close the socket in the finally block
            }
        }
    }

    private static void measurements(DatagramSocket socket, InetAddress destAddr, int destPort) throws IOException {
        long totalDuration = 0; // Duration between the time when number N is sent and the time response with the string S is received.
        long maxDuration = Long.MIN_VALUE;
        long minDuration = Long.MAX_VALUE;

        // Results for 5 different numbers
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 65536) - 32768; // Generate random number in the range [-32768, 32767]

            // Convert the random number to a byte array
            byte[] numberBytes = new byte[2];
            numberBytes[0] = (byte) ((random >> 8) & 0xFF); // Most significant byte
            numberBytes[1] = (byte) (random & 0xFF); // Least significant byte

            // Display byte per byte in hexadecimal
            System.out.print("Sending number N as hexadecimal: ");
            for (byte b : numberBytes) {
                System.out.print(String.format("%02X ", b));
            }
            System.out.println();

            // Create UDP packet containing the number N
            DatagramPacket message = new DatagramPacket(numberBytes, numberBytes.length,
                    destAddr, destPort);

            long start = System.currentTimeMillis(); // Starting time

            // Send the packet
            socket.send(message);

            // Receive and display the server's response
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket);

            long end = System.currentTimeMillis(); // Time when received

            long duration = end - start; // Total time from when N is sent and when something is received

            minDuration = Math.min(minDuration, duration);
            maxDuration = Math.max(maxDuration, duration);

            totalDuration += duration;

            // Display measurements
            String receivedString = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-16");

            System.out.println("Received: " + receivedString);
            System.out.println("Duration (milliseconds): " + duration);
        }

        // Display the measurements
        System.out.println("Min Duration (milliseconds): " + minDuration);
        System.out.println("Max Duration (milliseconds): " + maxDuration);
        System.out.println("Average Duration (milliseconds): " + (totalDuration / 5));
    }

    private static int getUserInput(BufferedReader reader) {
        int number;
        while (true) {
            try {
                System.out.print("Enter a number (-32768 to 32767): ");
                number = Integer.parseInt(reader.readLine());
                if (number >= -32768 && number <= 32767) {
                    return number; // Valid input, return the number
                } else {
                    System.out.println("Number is not in the valid range.");
                }
            } catch (NumberFormatException | IOException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}

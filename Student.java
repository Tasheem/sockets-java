import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

// Me: Tasheem Hargrove
// Partner: James Jester

public class Student {
    public static void main(String[] args) {
        String hostName = "174.24.46.251";
        int portNumber = 3310;

        try {
            Socket s1 = new Socket(hostName, portNumber);
            String studentID = "thargro1";
            PrintWriter s1Out = new PrintWriter(s1.getOutputStream());
            s1Out.write(studentID);
            s1Out.flush();

            BufferedReader s1In = new BufferedReader(
                new InputStreamReader(s1.getInputStream()));
            char[] recv = new char[5];
            int byteRead = 0;
            int byteLeft = 5;
            do {
                byteRead = s1In.read(recv, byteRead, byteLeft);
            } while ((byteLeft -= byteRead) > 0);
            String response = new String(recv);
            System.out.println("TCP Response: " + response);

            s1.close();

            int s2Port = Integer.parseInt(response);
            System.out.print("Creating TCP socket for listening and accepting connection...");
            ServerSocket s2 = new ServerSocket(s2Port);
		    System.out.println("Done");

            System.out.println("\nReady to accept connection on port "+s2Port);
		    System.out.println("Waiting for connection...");

            Socket acceptSocket = s2.accept();
            s2.close();

            BufferedReader s2Input = new BufferedReader(
                new InputStreamReader(acceptSocket.getInputStream()));
            char[] s2Buffer = new char[12];
            int bytesRead = 0;
            int bytesLeft = 12;
            do{
                bytesRead = s2Input.read(s2Buffer, bytesRead, bytesLeft);
            }while ((byteLeft -= byteRead) > 0);

            String robotMessage = new String(s2Buffer);
            System.out.println("Robot Message Received: "+ robotMessage);
            int robotUdpPort = Integer.parseInt(robotMessage.substring(0, 5));
            System.out.println("Robot UDP Port: " + robotUdpPort);

            int studentUdpPort = Integer.parseInt(robotMessage.substring(6, 11));
            System.out.print("\nPreparing socket s3 <"+studentUdpPort+">...");
            DatagramSocket s3 = new DatagramSocket(studentUdpPort);
            System.out.println("Done");

            System.out.println("Sending UDP packet:");
            int num = new Random().nextInt(10);
            if(num < 5)
                num += 5;

            String messageToTransmit = String.valueOf(num);
            System.out.println("Message to transmit: "+messageToTransmit);
            DatagramPacket sendPacket = new DatagramPacket(messageToTransmit.getBytes(), 0,  
                messageToTransmit.length(), InetAddress.getByName(hostName), 
                robotUdpPort);
            
            s3.send(sendPacket);

            byte[] buff = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buff, 0,  buff.length);
            s3.receive(receivePacket);
            String xxx = new String(receivePacket.getData(), 0, receivePacket.getLength());
            byte[] xxxBytes = xxx.getBytes();
            int n = (int)xxxBytes[0]-48;
            System.out.println("Get robot xxx string = " + xxx + " n = "+n);

            System.out.println("Sending xxx string back...");
            DatagramPacket sendRobotXxxString = new DatagramPacket(xxxBytes, 0,  
                xxx.length(), InetAddress.getByName(hostName), 
                robotUdpPort);

            for (int i=0; i<5; i++){
                s3.send(sendRobotXxxString);
                Thread.sleep(1000);
                System.out.println("UDP packet "+(i+1)+" sent");
            }
            System.out.println("Done");

            s3.close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host: ");
            System.out.println(e.getMessage());
            return;
        } catch (IOException io) {
            System.out.println("IO Error: ");
            System.out.println(io.getMessage());
            return;
        } catch(IllegalArgumentException e) {
            System.out.println("Illegal Argument: ");
            System.out.println(e.getMessage());
            return;
        } catch(InterruptedException e) {
            System.out.println("Interrupted Exception: ");
            System.out.println(e.getMessage());
            return;
        }
    }
}
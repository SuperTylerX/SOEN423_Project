package repicas.replica4.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Network {
    public static String sendUDP(String content, String targetHost, int port) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] m = content.getBytes();
            InetAddress aHost = InetAddress.getByName(targetHost);
            DatagramPacket request =
                    new DatagramPacket(m, content.length(), aHost, port);
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            return new String(reply.getData()).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}

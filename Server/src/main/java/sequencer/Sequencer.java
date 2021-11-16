package sequencer;

import common.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sequencer {

    public static void main(String[] args) {
        try {

            // TODO: Get the request from FE
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
            socket.joinGroup(group);
            String line = null;
            while ((line = reader.readLine()) != null) {

                //
                byte[] buff = line.getBytes();


                DatagramPacket packet = new DatagramPacket(buff, buff.length, group, Setting.REPLICA_MULTICAST_PORT);
                socket.send(packet);
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

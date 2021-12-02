package sequencer;

import common.Setting;
import packet.Packet;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MsgResender extends Thread {

    private int count = 0;
    private final Packet packet;

    public MsgResender(Packet p) {
        packet = p;
    }

    @Override
    public void run() {
        while (true) {

            // Wait 3 seconds(Timeout time = 3s)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (count > 5) {
                System.out.println("Warning: timeout more than 5 times!");
                break;
            }

            if (packet.ACKs.contains(1) && packet.ACKs.contains(2) && packet.ACKs.contains(3) && packet.ACKs.contains(4)) {
                System.out.println("Received All ACKs. Close the timer and re-sender");
                break;
            }

            // ACK timeout, Re-multicast the request to all replicas
            try {
                MulticastSocket socket = new MulticastSocket();
                InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
                socket.joinGroup(group);

                byte[] buff = SerializedObjectConverter.toByteArray(packet);
                DatagramPacket packet = new DatagramPacket(buff, buff.length, group, Setting.REPLICA_MULTICAST_PORT);
                socket.send(packet);
                socket.close();
                count++;

            } catch (IOException e) {
                throw new RuntimeException("Failed");
            }
        }
    }
}

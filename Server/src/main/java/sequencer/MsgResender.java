package sequencer;

import common.Setting;
import packet.Packet;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MsgResender extends Thread {

    int count = 0;
    Packet packet;

    public MsgResender(Packet p) {
        packet = p;
    }

    @Override
    public void run() {
        while (true) {

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
                System.out.println("Received All RESPONSES");
                System.out.println("ALL RESPONSES ARE THE SAME");
                break;

                // if not the same, identify problematic replica and initiate protocol (start_error_count())
            }

            if (packet.ACKs.contains(1) && packet.ACKs.contains(2) && packet.ACKs.contains(3)) {

                // identify failed replica
                System.out.println("received only 3 responses - CRASH FAILURE");
                System.out.println("trigger its replica manager");
                break;
            }

            try {
                MulticastSocket socket = new MulticastSocket();
                InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
                socket.joinGroup(group);
                String line = null;

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

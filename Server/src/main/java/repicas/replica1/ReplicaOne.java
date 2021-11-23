package repicas.replica1;

import common.Setting;
import packet.Packet;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReplicaOne {
    public static void main(String[] args) {

        Server replicaRunnable = new Server();
        new Thread(replicaRunnable).start();
        try {
            MulticastSocket socket = new MulticastSocket(Setting.REPLICA_MULTICAST_PORT);
            InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
            byte[] buff = new byte[1024];
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                Packet sp = (Packet) SerializedObjectConverter.toObject(packet.getData());
                String ip = packet.getAddress().getHostAddress();
                System.out.println("ip:" + ip + " says: " + sp);
                //TODO: Reply ACK

                replicaRunnable.tasks.add(sp);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

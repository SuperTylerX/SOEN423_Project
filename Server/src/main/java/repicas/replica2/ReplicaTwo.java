package repicas.replica2;

import common.Setting;
import packet.Packet;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class ReplicaTwo {
    public static void main(String[] args) {

        Server replicaRunnable = new Server();
        new Thread(replicaRunnable).start();
        try {
            MulticastSocket socket = new MulticastSocket(Setting.REPLICA_MULTICAST_PORT);
            InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
            byte[] buff = new byte[1024];
            socket.joinGroup(group);
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                Packet sp = (Packet) SerializedObjectConverter.toObject(packet.getData());
                String ip = packet.getAddress().getHostAddress();
                System.out.println("ip:" + ip + " says: " + sp);
                //Reply ACK
                replyACK(sp.getSequenceNumber());

                replicaRunnable.tasks.add(sp);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }

    public static void replyACK(int sequencerNumber) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put("ReplicaName", 2);
        p.put("SequencerNumber", sequencerNumber);
        byte[] buf = SerializedObjectConverter.toByteArray(p);
        try {
            InetAddress address = InetAddress.getByName(Setting.SEQUENCER_IP);
            DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, Setting.SEQUENCER_ACK_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

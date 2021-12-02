package repicas.replica4;

import common.Setting;
import packet.Packet;
import replicamanger.ReplicaManager;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class ReplicaFour {

    final static private int replicaIndex = 4;
    public static Server replicaRunnable;
    static Thread replicaThread;
    static ReplicaManager replicaManager = new ReplicaManager(replicaIndex);

    public static void main(String[] args) {

        replicaRunnable = new Server();
        replicaThread = new Thread(replicaRunnable);
        replicaThread.start();

        Thread replicaManagerThread = new Thread(replicaManager);
        replicaManagerThread.start();
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1);
//                } catch (Exception e) {
//
//                }
//                Scanner sc = new Scanner(System.in);
//                if (sc.nextLine().equals("shutdown")) {
//                    shutdown();
//                }
//            }
//
//        }).start();
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
                replicaManager.packetsHistory.add(sp);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }

    public static void replyACK(int sequencerNumber) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put("ReplicaName", replicaIndex);
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

    public static void shutdownAndRestart(ArrayList<Packet> packets) {
        System.out.println("Shutdown");
        replicaRunnable.shutdown();
        replicaThread.stop();

        replicaRunnable = new Server();
        replicaThread = new Thread(replicaRunnable);
        replicaThread.start();

        replicaRunnable.setPackets(packets);
    }

}

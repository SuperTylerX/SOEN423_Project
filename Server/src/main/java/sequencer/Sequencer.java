package sequencer;

import common.Setting;
import packet.Packet;
import packet.parameter.*;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Sequencer {

    static Map<Integer, Packet> deliveryMap = new ConcurrentHashMap<>();
    static AtomicInteger sequence_number = new AtomicInteger(0);

    public static void main(String[] args) {
        try {

            // Run ACKReceiver Thread
            new ACKReceiver().start();

            // Get the request from FE
            DatagramSocket FE_socket = new DatagramSocket(Setting.SEQUENCER_PORT);

            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] buf = new byte[1024];
                DatagramPacket FE_packet = new DatagramPacket(buf, buf.length);
                FE_socket.receive(FE_packet);
                System.out.println("RECEIVED SEQUENCER PSVM");

                new Thread(() -> {
                    try {
                        Packet request = (Packet) SerializedObjectConverter.toObject(FE_packet.getData());
                        int seq = sequence_number.getAndIncrement();
                        request.setSequenceNumber(seq);

                        MulticastSocket socket = new MulticastSocket();
                        InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
                        socket.joinGroup(group);

                        deliveryMap.put(seq, request);

                        byte[] buff = SerializedObjectConverter.toByteArray(request);
                        DatagramPacket packet = new DatagramPacket(buff, buff.length, group, Setting.REPLICA_MULTICAST_PORT);
                        socket.send(packet);
                        socket.close();
                        new MsgResender(request).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

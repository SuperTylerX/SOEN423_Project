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

public class Sequencer {

    static Map<Integer, Packet> deliveryMap = new ConcurrentHashMap<>();
    static int sequence_number = 0;

    public static void main(String[] args) {
        try {

            // Run ACKReceiver Thread
            new ACKReceiver().start();

            // TODO: Get the request from FE
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
                        request.setSequenceNumber(sequence_number);

                        MulticastSocket socket = new MulticastSocket();
                        InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
                        socket.joinGroup(group);

                        deliveryMap.put(sequence_number, request);

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

//            OperationParameter op = new CreateRoomParameter("102", "2021-01-21", "7:00-9:00", "KKLA1000");
//            Packet sp = new Packet(1, Operation.CREATE_ROOM, op, "KKL");
//            OperationParameter op = new DeleteRoomParameter("102", "2021-01-21", "7:00-9:00", "KKLA1000");
//            Packet sp = new Packet(3, Operation.DELETE_ROOM, op, "KKL");
//            OperationParameter op = new GetAvailableTimeSlotParameter("2021-01-21");
//            Packet sp = new Packet(0, Operation.GET_AVAILABLE_TIME_SLOT, op, "DVL");


        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

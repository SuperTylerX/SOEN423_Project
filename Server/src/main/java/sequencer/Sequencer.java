package sequencer;

import common.Setting;
import packet.Packet;
import packet.parameter.*;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sequencer {

    public static void main(String[] args) {
        try {

            // TODO: Get the request from FE
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
            socket.joinGroup(group);
            String line = null;

            // Mock some requests

//            OperationParameter op = new CreateRoomParameter("102", "2021-01-21", "7:00-9:00", "KKLA1000");
//            Packet sp = new Packet(1, Operation.CREATE_ROOM, op, "KKL");
//            OperationParameter op = new DeleteRoomParameter("102", "2021-01-21", "7:00-9:00", "KKLA1000");
//            Packet sp = new Packet(3, Operation.DELETE_ROOM, op, "KKL");
            OperationParameter op = new GetAvailableTimeSlotParameter("2021-01-21");
            Packet sp = new Packet(4, Operation.GET_AVAILABLE_TIME_SLOT, op, "DVL");

            byte[] buff = SerializedObjectConverter.toByteArray(sp);
            DatagramPacket packet = new DatagramPacket(buff, buff.length, group, Setting.REPLICA_MULTICAST_PORT);
            socket.send(packet);

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

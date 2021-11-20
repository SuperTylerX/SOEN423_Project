package sequencer;

import common.Setting;
import packet.Packet;
import packet.parameter.CreateRoomParameter;
import packet.parameter.Operation;
import packet.parameter.OperationParameter;
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

            OperationParameter op = new CreateRoomParameter("101", "2021-01-21", "7:00-9:00", "DVLA1000");
            Packet sp = new Packet(0, Operation.CREATE_ROOM, op, "DVL");

            byte[] buff = SerializedObjectConverter.toByteArray(sp);
            DatagramPacket packet = new DatagramPacket(buff, buff.length, group, Setting.REPLICA_MULTICAST_PORT);
            socket.send(packet);

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

package replicamanger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.Setting;
import packet.Packet;
import packet.parameter.GetAvailableTimeSlotParameter;
import packet.parameter.OperationParameter;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ReplicaManagerTest {
    public static void main(String[] args) {
        try{
            OperationParameter op = new GetAvailableTimeSlotParameter("2021-01-21");

            //String replacePacketsReply = replacePackets(Setting.REPLICA3_IP, Setting.REPLICA3_PORT, packets);
            //System.out.println(replacePacketsReply);

            // String sendPacketsToReply = sendPacketsTo(Setting.REPLICA1_IP, Setting.REPLICA1_PORT, Setting.REPLICA2_IP, Setting.REPLICA2_PORT);
            //System.out.println(sendPacketsToReply);

            String rebootReply = reboot(4);
            System.out.println(rebootReply);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static String reboot(int replicaIndex) throws Exception {
        switch(replicaIndex) {
            case 1:
                return sendPacketsTo(Setting.REPLICA2_IP, Setting.REPLICA2_PORT, Setting.REPLICA1_IP, Setting.REPLICA1_PORT);
            case 2:
                return sendPacketsTo(Setting.REPLICA3_IP, Setting.REPLICA3_PORT, Setting.REPLICA2_IP, Setting.REPLICA2_PORT);
            case 3:
                return sendPacketsTo(Setting.REPLICA4_IP, Setting.REPLICA4_PORT, Setting.REPLICA3_IP, Setting.REPLICA3_PORT);
            case 4:
                return sendPacketsTo(Setting.REPLICA1_IP, Setting.REPLICA1_PORT, Setting.REPLICA4_IP, Setting.REPLICA4_PORT);
            default:
                throw new Exception("Replica index out of range");
        }
    }

    private static String sendPacketsTo(String ip, int port, String targetIp, int targetPort) throws IOException {
        //ask another RM to send packets to another RM
        DatagramSocket socket;
        InetAddress address = InetAddress.getByName(ip);

        String message = ReplicaManagerOperations.SEND_PACKETS_TO.name() + "," + targetIp + "," + targetPort;
        byte[] buff = SerializedObjectConverter.toByteArray(message);
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);

        socket = new DatagramSocket();
        socket.send(packet);

        byte[] data = new byte[1024];
        DatagramPacket reply = new DatagramPacket(data, data.length);
        socket.receive(reply);

        String replyMessage = new String(reply.getData(), 0, reply.getLength());
        socket.close();
        return replyMessage;
    }

    private static String replacePackets(String ip, int port, ArrayList<Packet> packets) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(
                OperationAdapterFactory.operationAdapterFactory).create();
        String json = gson.toJson(packets);
        String message = ReplicaManagerOperations.REPLACE_PACKETS_AND_REBOOT.name() + "," + json;

        DatagramSocket socket;
        InetAddress address = InetAddress.getByName(ip);
        byte[] buff = SerializedObjectConverter.toByteArray(message);
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);

        socket = new DatagramSocket();
        socket.send(packet);

        byte[] data = new byte[1024];
        DatagramPacket reply = new DatagramPacket(data, data.length);
        socket.receive(reply);

        String replyMessage = new String(reply.getData(), 0, reply.getLength());
        socket.close();

        return replyMessage;
    }
}

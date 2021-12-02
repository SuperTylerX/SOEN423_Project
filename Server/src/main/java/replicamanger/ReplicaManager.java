package replicamanger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.Setting;
import packet.Packet;
import repicas.replica1.ReplicaOne;
import repicas.replica2.ReplicaTwo;
import repicas.replica3.ReplicaThree;
import repicas.replica4.ReplicaFour;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class ReplicaManager implements Runnable {

    public ArrayList<Packet> packetsHistory;
    String myIp;
    int myPort;
    int replicaIndex;

    public ReplicaManager(int replicaIndex) {
        this.packetsHistory = new ArrayList<>();
        this.replicaIndex = replicaIndex;

        switch (replicaIndex) {
            case 1:
                myIp = Setting.REPLICA1_IP;
                myPort = Setting.REPLICA1_PORT;
                break;
            case 2:
                myIp = Setting.REPLICA2_IP;
                myPort = Setting.REPLICA2_PORT;
                break;
            case 3:
                myIp = Setting.REPLICA3_IP;
                myPort = Setting.REPLICA3_PORT;
                break;
            case 4:
                myIp = Setting.REPLICA4_IP;
                myPort = Setting.REPLICA4_PORT;
                break;
        }
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(myPort);
            byte[] buffer = new byte[10000];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

//                String s = (String) SerializedObjectConverter.toObject(packet.getData());
                HashMap<String, Object> req = (HashMap<String, Object>) SerializedObjectConverter.toObject(packet.getData());
                System.out.println(req);

//                String messageHeader = s;
//                if (messageHeader.contains(",")) {
//                    messageHeader = messageHeader.substring(0, s.indexOf(','));
//                }

                if (req.get("Operation") == ReplicaManagerOperations.REPLACE_PACKETS_AND_REBOOT) {
                    System.out.println("RM: REPLACE_PACKETS_AND_REBOOT request received");

                    ArrayList<Packet> tempPackets = (ArrayList<Packet>) req.get("Packets");

                    //reset replica
                    switch (replicaIndex) {
                        case 1:
                            ReplicaOne.shutdownAndRestart(tempPackets);
                            break;
                        case 2:
                            ReplicaTwo.shutdownAndRestart(tempPackets);
                            break;
                        case 3:
                            ReplicaThree.shutdownAndRestart(tempPackets);
                            break;
                        case 4:
                            ReplicaFour.shutdownAndRestart(tempPackets);
                            break;
                    }

                    //update packetsHistory
                    packetsHistory = new ArrayList<>();
                    packetsHistory.addAll(tempPackets);

                    //send reply
                    String message = "OK";
                    DatagramPacket reply = new DatagramPacket(message.getBytes(), message.length(),
                            packet.getAddress(), packet.getPort());
                    socket.send(reply);

                    System.out.println("RM: REPLACE_PACKETS_AND_REBOOT Done");
                    continue;
                }

                if (req.get("Operation") == ReplicaManagerOperations.SEND_PACKETS_TO) {
                    System.out.println("RM: SEND_PACKETS_TO request received");

                    // replace packet in the other replica
                    String replacePacketsAnswer = replacePackets((String) req.get("TargetIp"), (int) req.get("TargetPort"), packetsHistory);


                    // send reply to FE
                    String replyMessage = "OK";
                    DatagramPacket DatagramPacketReply = new DatagramPacket(replyMessage.getBytes(), replyMessage.length(),
                            packet.getAddress(), packet.getPort());
                    socket.send(DatagramPacketReply);

                    System.out.println("RM: SEND_PACKETS_TO.REPLACE_PACKETS_AND_REBOOT: " + replacePacketsAnswer);
                    System.out.println("RM: SEND_PACKETS_TO Done");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String replacePackets(String ip, int port, ArrayList<Packet> packets) throws IOException {

        HashMap<String, Object> req = new HashMap<>();
        req.put("Operation", ReplicaManagerOperations.REPLACE_PACKETS_AND_REBOOT);
        req.put("Packets", packets);
        System.out.println(req);
        DatagramSocket socket;
        InetAddress address = InetAddress.getByName(ip);
        byte[] buff = SerializedObjectConverter.toByteArray(req);
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

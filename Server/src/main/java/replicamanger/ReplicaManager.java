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

public class ReplicaManager implements Runnable {

    public ArrayList<Packet> packetsHistory;
    String myIp;
    int myPort;
    int replicaIndex;

    public ReplicaManager(int replicaIndex) {
        this.packetsHistory = new ArrayList<>();
        this.replicaIndex = replicaIndex;

        switch(replicaIndex) {
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
            byte[] buffer = new byte[1000];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String s = (String) SerializedObjectConverter.toObject(packet.getData());

                String messageHeader = s;
                if (messageHeader.contains(",")){
                    messageHeader = messageHeader.substring(0,s.indexOf(','));
                }

                if(messageHeader.equals(ReplicaManagerOperations.REPLACE_PACKETS_AND_RESET.name())){
                    System.out.println("RM: REPLACE_PACKETS_AND_RESET request received");
                    String json = s.substring(s.indexOf(",")+1); // remove header

                    Type listType = new TypeToken<ArrayList<Packet>>(){}.getType();
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(OperationAdapterFactory.operationAdapterFactory).create();

                    ArrayList<Packet> tempPackets = gson.fromJson(json, listType);

                    //reset replica
                    switch(replicaIndex) {
                        case 1:
                            //ReplicaOne.replicaRunnable.resetServer(tempPackets);
                            break;
                        case 2:
                            //ReplicaTwo.replicaRunnable.resetServer(tempPackets);
                            break;
                        case 3:
                            ReplicaThree.shutdownAndRestart(tempPackets);
                            //
                            break;
                        case 4:
                            //ReplicaFour.replicaRunnable.resetServer(tempPackets);
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

                    System.out.println("RM: REPLACE_PACKETS_AND_RESET Done");
                    continue;
                }

                if(messageHeader.equals(ReplicaManagerOperations.SEND_PACKETS_TO.name())){
                    System.out.println("RM: SEND_PACKETS_TO request received");
                    String[] parameters = s.split(",");

                    //replace packet in the other replica
                    String replacePacketsAnswer = replacePackets(parameters[1], Integer.parseInt(parameters[2]), packetsHistory);

                    //send reply
                    String replyMessage = "OK";
                    DatagramPacket DatagramPacketReply = new DatagramPacket(replyMessage.getBytes(), replyMessage.length(),
                            packet.getAddress(), packet.getPort());
                    socket.send(DatagramPacketReply);

                    System.out.println("RM: SEND_PACKETS_TO.REPLACE_PACKETS_AND_RESET: " + replacePacketsAnswer);
                    System.out.println("RM: SEND_PACKETS_TO Done");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String replacePackets(String ip, int port, ArrayList<Packet> packets) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(
                OperationAdapterFactory.operationAdapterFactory).create();
        String json = gson.toJson(packets);
        String message = ReplicaManagerOperations.REPLACE_PACKETS_AND_RESET.name() + "," + json;

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

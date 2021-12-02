package frontend;

import common.Setting;
import packet.Packet;
import replicamanger.ReplicaManagerOperations;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SFT_HA {

    public static String make_SFT_HA(Packet request) {

        String identifier = ResponseWaitingList.assignIdentifier().toString();
        request.setIdentifier(identifier);

        byte[] buff = SerializedObjectConverter.toByteArray(request);

        try {
            InetAddress address = InetAddress.getByName(Setting.SEQUENCER_IP);
            DatagramPacket dataGramPacket = new DatagramPacket(buff, buff.length, address, Setting.SEQUENCER_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTimeStamp = new Date().getTime();

        while (true) {

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CopyOnWriteArrayList<ConcurrentHashMap<String, String>> responses = ResponseWaitingList.responseMap.get(identifier);
            if (responses == null) continue;
            if (responses.size() == 4) {
                System.out.println("Get 4 responses from replicas");
                System.out.println("Now do the Byzantine algorithm for 4 replicas");
                boolean isConsistent = true;
                for (int i = 1; i < 4; i++) {
                    if (!responses.get(i).get("Result").equals(responses.get(0).get("Result"))) {
                        isConsistent = false;
                        break;
                    }
                }

                if (isConsistent) {
                    System.out.println("Get 4 consistent responses from replicas");
                    return responses.get(0).get("Result");
                } else {

                    // identify replica that sent the inconsistent result
                    for (int i = 0; i < 4; i++) {
                        int count = 0;
                        for (int j = 0; j < 4; j++) {
                            if (i != j && !responses.get(i).get("Result").equals(responses.get(j).get("Result"))) {
                                count++;
                            }
                        }
                        if (count == 3) {
                            System.out.println("Get inconsistent response from replica " + responses.get(i));
                            // notify manager here
                            try {
                                reboot(responses.get(i).get("ReplicaName"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 1 inconsistent response
                            return responses.get((i + 1) % 4).get("Result");
                        }
                    }
                    System.out.println("We can not identify which one is inconsistent");
                    return "System is down, please try again later...";
                }
            }

            if (new Date().getTime() - startTimeStamp > 3000) {
                if (responses.size() == 3) {
                    System.out.println("Time out... Entered crash protocol");
                    System.out.println("Get 3 responses. Now identifying which replica is crashed...");
                    ArrayList<String> finding_bad_replica = new ArrayList<>();
                    finding_bad_replica.add("R1");
                    finding_bad_replica.add("R2");
                    finding_bad_replica.add("R3");
                    finding_bad_replica.add("R4");

                    for (int i = 0; i < 3; i++) {  // looping to identify crashed replica
                        finding_bad_replica.remove(responses.get(i).get("ReplicaName"));
                    }

                    System.out.println("Crashed replica is " + finding_bad_replica.get(0));
                    // send this to ReplicaManager
                    try {
                        reboot(finding_bad_replica.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // one crashed replica + 3 GOOD RESPONSES


                    System.out.println("Now do the Byzantine algorithm for 3 replicas");
                    boolean isConsistent = true;
                    for (int i = 1; i < 3; i++) {
                        if (!responses.get(i).get("Result").equals(responses.get(0).get("Result"))) {
                            isConsistent = false;
                            break;
                        }
                    }

                    if (isConsistent) {
                        System.out.println("Get 3 consistent responses from replicas");
                        return responses.get(0).get("Result");
                    } else {

                        // identify replica that sent inconsistent result
                        for (int i = 0; i < 3; i++) {
                            int count = 0;
                            for (int j = 0; j < 3; j++) {
                                if (i != j && !responses.get(i).get("Result").equals(responses.get(j).get("Result"))) {
                                    count++;
                                }
                            }
                            if (count == 2) {
                                System.out.println("Get inconsistent response from replica " + responses.get(i));
                                // notify manager here
                                try {
                                    reboot(responses.get(i).get("ReplicaName"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 1 inconsistent response and 1 crashed replica + 2 GOOD RESPONSES
                                return responses.get((i + 1) % 3).get("Result");
                            }

                        }
                        System.out.println("We can not identify which one is inconsistent");
                        return "System is down, please try again later...";
                    }
                }

                System.out.println("We can not identify which one is inconsistent");
                return "System is down, please try again later...";

            }
        }
    }

    private static String reboot(String replicaIndex) throws Exception {
        switch (replicaIndex) {
            case "R1":
                return sendPacketsTo(Setting.REPLICA2_IP, Setting.REPLICA2_PORT, Setting.REPLICA1_IP, Setting.REPLICA1_PORT);
            case "R2":
                return sendPacketsTo(Setting.REPLICA3_IP, Setting.REPLICA3_PORT, Setting.REPLICA2_IP, Setting.REPLICA2_PORT);
            case "R3":
                return sendPacketsTo(Setting.REPLICA4_IP, Setting.REPLICA4_PORT, Setting.REPLICA3_IP, Setting.REPLICA3_PORT);
            case "R4":
                return sendPacketsTo(Setting.REPLICA1_IP, Setting.REPLICA1_PORT, Setting.REPLICA4_IP, Setting.REPLICA4_PORT);
            default:
                throw new Exception("Replica index out of range");
        }
    }

    private static String sendPacketsTo(String ip, int port, String targetIp, int targetPort) throws IOException {
        //ask another RM to send packets to another RM
        DatagramSocket socket;
        InetAddress address = InetAddress.getByName(ip);

        HashMap<String, Object> req = new HashMap<>();
        req.put("Operation", ReplicaManagerOperations.SEND_PACKETS_TO);
        req.put("TargetIp", targetIp);
        req.put("TargetPort", targetPort);

        System.out.println(req);
//        String message = ReplicaManagerOperations.SEND_PACKETS_TO.name() + "," + targetIp + "," + targetPort;
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

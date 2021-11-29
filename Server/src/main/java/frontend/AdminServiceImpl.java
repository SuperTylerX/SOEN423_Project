package frontend;

import common.Setting;
import packet.Packet;
import packet.parameter.CreateRoomParameter;
import packet.parameter.Operation;
import packet.parameter.OperationParameter;
import utils.SerializedObjectConverter;

import javax.jws.WebService;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@WebService(name = "AdminService", endpointInterface = "frontend.AdminService")
public class AdminServiceImpl implements AdminService {


    @Override
    public String createRoom(String roomNumber, String date, String timeSlot, String userID) {
        OperationParameter op = new CreateRoomParameter(roomNumber, date, timeSlot, userID);

        Packet request = new Packet(Operation.CREATE_ROOM, op, userID.substring(0, 3));
        String identifier = ResponseWaitingList.assignIdentifier().toString();
        request.setIdentifier(identifier);
        String response = "debug";


        byte[] buff = SerializedObjectConverter.toByteArray(request);

        try {
            InetAddress address = InetAddress.getByName(Setting.SEQUENCER_IP);
            DatagramPacket dataGramPacket = new DatagramPacket(buff, buff.length, address, Setting.SEQUENCER_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CopyOnWriteArrayList<ConcurrentHashMap<String, String>> responses = ResponseWaitingList.responseMap.get(identifier);
        if (responses.size() == 4) {

            boolean isConsistent = true;
            for (int i = 1; i < 4; i++) {
                if (!responses.get(i).get("Result").equals(responses.get(0).get("Result"))) {
                    isConsistent = false;
                    break;
                }
            }


            if (isConsistent) {
                System.out.println("consistent");
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
                        System.out.println("inconsistent replica response from " + responses.get(i));
                        // TODO: notify manager here
                        // 1 inconsistent response
                        return responses.get((i + 1) % 4).get("Result");
                    }
                }
                System.out.println("not consistent");
//                return ;
                // Byzantine Protocol
            }


        } else if (responses.size() == 3) {
            System.out.println("ENTERED CRASH PROTOCOL");
            ArrayList<String> finding_bad_replica = new ArrayList<>();
            finding_bad_replica.add("R1");
            finding_bad_replica.add("R2");
            finding_bad_replica.add("R3");
            finding_bad_replica.add("R4");

            for (int i = 0; i < 3; i++) {  // looping to identify crashed replica
                finding_bad_replica.remove(responses.get(i).get("ReplicaName"));
            }

            System.out.println("crashed replica " + finding_bad_replica.get(0));
            // TODO: send this to ReplicaManager
            // one crashed replica + 3 GOOD RESPONSES

            boolean isConsistent = true;
            for (int i = 1; i < 3; i++) {
                if (!responses.get(i).get("Result").equals(responses.get(0).get("Result"))) {
                    isConsistent = false;
                    break;
                }
            }

            if (isConsistent) {

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
                        System.out.println("inconsistent replica response from " + responses.get(i));
                        // TODO: notify manager here
                        // 1 inconsistent response and 1 crashed replica + 2 GOOD RESPONSES
                        return responses.get((i + 1) % 3).get("Result");
                    }
                }
            }
        }
        return "debug end of createRoom()";
    }

    @Override
    public String deleteRoom(String roomNumber, String date, String timeSlot, String userID) {

        return "debug";
    }

}

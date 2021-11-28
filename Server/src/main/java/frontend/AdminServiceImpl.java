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
import java.util.Date;
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
        if (responses.size() ==  3){
            boolean isConsistent = true;
            for (int i = 1 ; i < 3;i++){
                if (!responses.get(i).get("Result").equals(responses.get(0).get("Result"))){
                    isConsistent = false;
                    break;
                }
            }


            if (isConsistent){
                System.out.println("consistent");
                return responses.get(0).get("Result");
            }else{
                System.out.println("not consistent");
                // Byzantine Protocol
            }


        }
            // while(true)

            // listening for responses. 4 responses. response.id  == request.id


            return "debug";
    }

    @Override
    public String deleteRoom(String roomNumber, String date, String timeSlot, String userID) {

        return "debug";
    }

}

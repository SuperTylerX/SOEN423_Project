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

@WebService(name = "AdminService", endpointInterface = "frontend.AdminService")
public class AdminServiceImpl implements AdminService {

    @Override
    public String createRoom(String roomNumber, String date, String timeSlot, String userID) {
        OperationParameter op = new CreateRoomParameter(roomNumber, date, timeSlot, userID);
        Packet request = new Packet(Operation.CREATE_ROOM, op, userID.substring(0, 3));

        byte[] buff = SerializedObjectConverter.toByteArray(request);

        try {
            InetAddress address = InetAddress.getByName(Setting.SEQUENCER_IP);
            DatagramPacket dataGramPacket = new DatagramPacket(buff, buff.length, address, Setting.SEQUENCER_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }





        return "debug";
    }

    @Override
    public String deleteRoom(String roomNumber, String date, String timeSlot, String userID) {

        return "debug";
    }

}

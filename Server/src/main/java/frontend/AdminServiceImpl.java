package frontend;

import common.Setting;
import packet.Packet;
import packet.parameter.CreateRoomParameter;
import packet.parameter.DeleteRoomParameter;
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
        return SFT_HA.make_SFT_HA(request);
    }

    @Override
    public String deleteRoom(String roomNumber, String date, String timeSlot, String userID) {
        OperationParameter op = new DeleteRoomParameter(roomNumber, date, timeSlot, userID);
        Packet request = new Packet(Operation.DELETE_ROOM, op, userID.substring(0, 3));
        return SFT_HA.make_SFT_HA(request);
    }

}

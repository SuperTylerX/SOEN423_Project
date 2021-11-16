package repicas.replica1;

import common.Setting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Server {
    public static void main(String[] args) {

        AdminService adminService = new AdminService();
        adminService.campusCode = "DVL";

        try {
            MulticastSocket socket = new MulticastSocket(Setting.REPLICA_MULTICAST_PORT);
            InetAddress group = InetAddress.getByName(Setting.REPLICA_MULTICAST_IP);
            byte[] buff = new byte[1024];
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String ip = packet.getAddress().getHostAddress();
                System.out.println("ip:" + ip + " says: " + data);

                // TODO: handle the message
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed");
        }
    }
}

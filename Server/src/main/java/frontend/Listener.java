package frontend;

import common.Setting;
import packet.Packet;
import sequencer.Sequencer;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Listener extends Thread {
    public void run() {
        System.out.println("frontend listening");
        try {
            DatagramSocket socket = new DatagramSocket(Setting.FRONTEND_PORT);

            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("packet address " + packet.getAddress().getHostAddress());

                String response = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("FE Listener response " + response);
            }
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static utils.SerializedObjectConverter.toObject;

public class Listener extends Thread {
    public void run() {
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
                System.out.println("Received response from " + packet.getAddress().getHostAddress());

                HashMap<String, String> hm = (HashMap<String, String>) SerializedObjectConverter.toObject(packet.getData());
                System.out.println("Received: " + hm);

                ConcurrentHashMap<String, String> res = new ConcurrentHashMap<>(hm);
                String identifier = res.get("Identifier");

                if (!ResponseWaitingList.responseMap.containsKey(identifier)) {
                    ResponseWaitingList.responseMap.put(identifier, new CopyOnWriteArrayList<>());
                }

                ResponseWaitingList.responseMap.get(identifier).add(res);

            }
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

import common.Setting;
import javafx.util.Pair;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put("ReplicaName", 1);
        p.put("SequencerNumber", 0);
        byte[] buf = SerializedObjectConverter.toByteArray(p);
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, Setting.SEQUENCER_ACK_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(dataGramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

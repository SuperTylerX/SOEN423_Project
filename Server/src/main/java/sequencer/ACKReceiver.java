package sequencer;

import common.Setting;
import utils.SerializedObjectConverter;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ACKReceiver extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                InetAddress address = InetAddress.getByName("127.0.0.1");
                DatagramSocket socket = new DatagramSocket(Setting.SEQUENCER_ACK_PORT, address);
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                socket.close();
                new Thread(() -> {
                    Map<String, Integer> res = (HashMap<String, Integer>) SerializedObjectConverter.toObject(packet.getData());
                    System.out.println(packet.getAddress() + " says :" + res);
                    int ReplicaName = res.get("ReplicaName");
                    int SequencerNumber = res.get("SequencerNumber");
                    Sequencer.deliveryMap.get(SequencerNumber).ACKs.add(ReplicaName);
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

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
        try {
            DatagramSocket socket = new DatagramSocket(Setting.SEQUENCER_ACK_PORT);

            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                new Thread(() -> {
                    Map<String, Integer> res = (HashMap<String, Integer>) SerializedObjectConverter.toObject(packet.getData());
                    System.out.println(packet.getAddress() + " says :" + res);
                    int ReplicaName = res.get("ReplicaName");
                    int SequencerNumber = res.get("SequencerNumber");
                    Sequencer.deliveryMap.get(SequencerNumber).ACKs.add(ReplicaName);
                }).start();
            }
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

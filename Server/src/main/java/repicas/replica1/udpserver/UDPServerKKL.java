package repicas.replica1.udpserver;


import repicas.replica1.roommanager.RoomManagerKKL;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPServerKKL extends Thread implements UDPServer{
    int PORT;
    DatagramSocket aSocket;

    public UDPServerKKL(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        System.out.println("UDP is running...");
        try {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            aSocket = new DatagramSocket(PORT);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getData()), StandardCharsets.UTF_8));
                String method = br.readLine();

                String result = "";
                switch (method) {
                    case "getAvailableTimeSlot":
                        String param = br.readLine();
                        result = String.valueOf(RoomManagerKKL.getInstance().getAvailableTimeSlot(param));
                        break;
                    case "bookRoomRemote": {
                        String roomNumber = br.readLine();
                        String date = br.readLine();
                        String timeSlot = br.readLine();
                        String studentID = br.readLine();
                        result = RoomManagerKKL.getInstance().addRoomRecord(date, roomNumber, timeSlot, studentID);
                        break;
                    }
                    case "removeBookingRemote": {
                        String roomNumber = br.readLine();
                        String date = br.readLine();
                        String timeSlot = br.readLine();
                        String studentID = br.readLine();
                        result = RoomManagerKKL.getInstance().removeRoomRecord(date, roomNumber, timeSlot, studentID);
                        break;
                    }
                }

                byte[] bytesResult = result.getBytes(StandardCharsets.UTF_8);
                DatagramPacket reply = new DatagramPacket(bytesResult,
                        bytesResult.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    public void closePort(){
        this.stop();
        aSocket.close();
    }

}

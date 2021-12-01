package repicas.replica4;

import packet.Packet;
import packet.parameter.*;
import repicas.replica4.service.AdminService;
import repicas.replica4.service.StudentService;
import utils.SerializedObjectConverter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

    final static private int replicaIndex = 4;
    private int replicaSequenceNumber;
    final public CopyOnWriteArrayList<Packet> tasks;
    Boolean faulty = false;
    StudentService studentServiceDVL;
    StudentService studentServiceKKL;
    StudentService studentServiceWST;


    public Server() {
        replicaSequenceNumber = 0;
        tasks = new CopyOnWriteArrayList<>();
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            System.out.println("input 'crash' to crash R" + replicaIndex + " for testing");
            if (sc.nextLine().equals("crash")) {
                faulty = true;
            }
        }).start();
    }

    public void shutdown() {
        studentServiceDVL.shutdown();
        studentServiceKKL.shutdown();
        studentServiceWST.shutdown();
    }

    @Override
    public void run() {
        // Initialized Admin and Student Service
        AdminService adminServiceDVL = new AdminService("DVL");
        AdminService adminServiceKKL = new AdminService("KKL");
        AdminService adminServiceWST = new AdminService("WST");

        studentServiceDVL = new StudentService("DVL", Setting.DVL_UDP_SERVER_PORT);
        studentServiceKKL = new StudentService("KKL", Setting.KKL_UDP_SERVER_PORT);
        studentServiceWST = new StudentService("WST", Setting.WST_UDP_SERVER_PORT);

        while (true) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Packet task : tasks) {
                if (task.getSequenceNumber() == replicaSequenceNumber) {
                    // handle task
                    String result = "";
                    switch (task.getCampus()) {
                        case "DVL":
                            result = selectService(task, adminServiceDVL, studentServiceDVL);
                            break;
                        case "KKL":
                            result = selectService(task, adminServiceKKL, studentServiceKKL);
                            break;
                        case "WST":
                            result = selectService(task, adminServiceWST, studentServiceWST);
                            break;
                    }

                    System.out.println("Response: " + result);

                    HashMap<String, String> hm = new HashMap<>();

                    hm.put("Identifier", task.getIdentifier());
                    hm.put("ReplicaName", "R" + replicaIndex);
                    hm.put("Result", result);

                    System.out.println("Send HashMap to FE: " + hm);
                    System.out.println();

                    byte[] buff = SerializedObjectConverter.toByteArray(hm);

                    try {
                        InetAddress address = InetAddress.getByName(common.Setting.FRONTEND_IP);
                        if (faulty) {
                            address = InetAddress.getByName(common.Setting.FRONTEND_IP + 1); // For testing crash
                        }
                        DatagramPacket dataGramPacket = new DatagramPacket(buff, buff.length, address, common.Setting.FRONTEND_PORT);
                        DatagramSocket socket = new DatagramSocket();
                        socket.send(dataGramPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tasks.remove(task);
                    replicaSequenceNumber++;
                }
            }
        }
    }

    public void setPackets(ArrayList<Packet> tempPackets){
        tasks.clear();
        tasks.addAll(tempPackets);
        System.out.println("packets have been replaced");
    }

    public String selectService(Packet task, AdminService adminService, StudentService studentService) {
        if (task.getOperation() == Operation.CREATE_ROOM) {
            CreateRoomParameter params = (CreateRoomParameter) task.getOperationParameter();
            return adminService.createRoom(params.roomNumber, params.date, params.timeSlot, params.userID);

        } else if (task.getOperation() == Operation.DELETE_ROOM) {
            DeleteRoomParameter params = (DeleteRoomParameter) task.getOperationParameter();
            return adminService.deleteRoom(params.roomNumber, params.date, params.timeSlot, params.userID);

        } else if (task.getOperation() == Operation.BOOK_ROOM) {
            BookRoomParameter params = (BookRoomParameter) task.getOperationParameter();
            return studentService.bookRoom(params.campusName, params.roomNumber, params.date, params.timeSlot, params.studentID, params.orderDate);

        } else if (task.getOperation() == Operation.CANCEL_BOOKING) {
            CancelBookingParameter params = (CancelBookingParameter) task.getOperationParameter();
            return studentService.cancelBooking(params.bookingID, params.studentID);

        } else if (task.getOperation() == Operation.GET_AVAILABLE_TIME_SLOT) {
            GetAvailableTimeSlotParameter params = (GetAvailableTimeSlotParameter) task.getOperationParameter();
            return studentService.getAvailableTimeSlot(params.date);

        } else if (task.getOperation() == Operation.CHANGE_RESERVATION) {
            ChangeReservationParameter params = (ChangeReservationParameter) task.getOperationParameter();
            return studentService.changeReservation(params.bookingID, params.newCampusName, params.newRoomNo, params.newTimeSlot, params.studentID, params.orderDate);
        } else {
            return null;
        }
    }
}

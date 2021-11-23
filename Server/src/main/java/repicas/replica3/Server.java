package repicas.replica3;

import packet.Packet;
import packet.parameter.*;
import repicas.replica3.service.AdminService;
import repicas.replica3.service.StudentService;

import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

    private int replicaSequenceNumber;
    final public CopyOnWriteArrayList<Packet> tasks;

    public Server() {
        replicaSequenceNumber = 0;
        tasks = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        // Initialized Admin and Student Service
        AdminService adminServiceDVL = new AdminService("DVL");
        AdminService adminServiceKKL = new AdminService("KKL");
        AdminService adminServiceWST = new AdminService("WST");

        StudentService studentServiceDVL = new StudentService("DVL", Setting.DVL_UDP_SERVER_PORT);
        studentServiceDVL.start();
        StudentService studentServiceKKL = new StudentService("KKL", Setting.KKL_UDP_SERVER_PORT);
        studentServiceKKL.start();
        StudentService studentServiceWST = new StudentService("WST", Setting.WST_UDP_SERVER_PORT);
        studentServiceWST.start();

        while (true) {

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

                    System.out.println(result);
                    // done!
                    // TODO: Send the result to Frontend
                    tasks.remove(task);
                    replicaSequenceNumber++;
                }
            }
        }
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
            return studentService.bookRoom(params.campusName, params.roomNumber, params.date, params.timeSlot, params.studentID);

        } else if (task.getOperation() == Operation.CANCEL_BOOKING) {
            CancelBookingParameter params = (CancelBookingParameter) task.getOperationParameter();
            return studentService.cancelBooking(params.bookingID, params.studentID);

        } else if (task.getOperation() == Operation.GET_AVAILABLE_TIME_SLOT) {
            GetAvailableTimeSlotParameter params = (GetAvailableTimeSlotParameter) task.getOperationParameter();
            return studentService.getAvailableTimeSlot(params.date);

        } else if (task.getOperation() == Operation.CHANGE_RESERVATION) {
            ChangeReservationParameter params = (ChangeReservationParameter) task.getOperationParameter();
            return studentService.changeReservation(params.bookingID, params.newCampusName, params.newRoomNo, params.newTimeSlot, params.studentID);
        } else {
            return null;
        }
    }
}
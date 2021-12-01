package repicas.replica4.service;

import repicas.replica4.Setting;
import repicas.replica4.model.BookingRecord;
import repicas.replica4.roommanager.RoomManager;
import repicas.replica4.roommanager.RoomManagerDVL;
import repicas.replica4.roommanager.RoomManagerKKL;
import repicas.replica4.roommanager.RoomManagerWST;
import repicas.replica4.udpserver.UDPServer;
import repicas.replica4.udpserver.UDPServerDVL;
import repicas.replica4.udpserver.UDPServerKKL;
import repicas.replica4.udpserver.UDPServerWST;
import repicas.replica4.utils.Log;
import repicas.replica4.utils.Network;

import java.util.Date;

public class StudentService {

    public RoomManager roomManager;
    public final String campusCode;
    UDPServer udpThread;

    public StudentService(String campusCode, int port) {
        this.campusCode = campusCode;
        switch (campusCode) {
            case "DVL":
                roomManager = RoomManagerDVL.getInstance();
                udpThread = new UDPServerDVL(port);
                udpThread.start();
                break;
            case "KKL":
                roomManager = RoomManagerKKL.getInstance();
                udpThread = new UDPServerKKL(port);
                udpThread.start();
                break;
            case "WST":
                roomManager = RoomManagerWST.getInstance();
                udpThread = new UDPServerWST(port);
                udpThread.start();
                break;
        }

    }

    public String bookRoom(String campusName, String roomNumber, String date, String timeSlot, String studentID, long orderDate) {
        String result = "";
        if (campusName.equals(this.campusCode)) {
            result = roomManager.bookRoomLocal(roomNumber, date, timeSlot, studentID, campusName, orderDate);
        } else {
            result = roomManager.bookRoomRemote(roomNumber, date, timeSlot, studentID, campusName, orderDate);
        }
        Log.addLog(campusCode, "Date: " + new Date().toLocaleString());
        Log.addLog(campusCode, "\r\nRequest Type: Book Room");
        Log.addLog(campusCode, "\r\nParameter: " + roomNumber + ", " + date + ", " + timeSlot + ", " + studentID);
        Log.addLog(campusCode, "\r\n" + result + "\r\n\r\n");
        return result;
    }

    public String getAvailableTimeSlot(String date) {
        String localResult = String.valueOf(roomManager.getAvailableTimeSlot(date));
        String DVL_Result = "", WST_Result = "", KKL_Result = "";
        switch (campusCode) {
            case "DVL":
                KKL_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.KKL_HOSTNAME, Setting.KKL_UDP_SERVER_PORT);
                WST_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.WST_HOSTNAME, Setting.WST_UDP_SERVER_PORT);
                DVL_Result = localResult;
                break;
            case "KKL":
                DVL_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.DVL_HOSTNAME, Setting.DVL_UDP_SERVER_PORT);
                WST_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.WST_HOSTNAME, Setting.WST_UDP_SERVER_PORT);
                KKL_Result = localResult;
                break;
            case "WST":
                DVL_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.DVL_HOSTNAME, Setting.DVL_UDP_SERVER_PORT);
                KKL_Result = Network.sendUDP("getAvailableTimeSlot\r\n" + date + "\r\n", Setting.KKL_HOSTNAME, Setting.KKL_UDP_SERVER_PORT);
                WST_Result = localResult;
                break;
        }
        return "DVL: " + DVL_Result + " KKL: " + KKL_Result + " WST: " + WST_Result;
    }

    public String cancelBooking(String bookingID, String studentID) {
        BookingRecord bookingRecord = roomManager.findRecord(bookingID);
        if (bookingRecord == null) {
            return "Failed! No such record found!";
        }
        String result = "";
        if (bookingRecord.campusName.equals(campusCode)) {
            result = roomManager.cancelBookingLocal(bookingID, studentID);
        } else {
            result = roomManager.cancelBookingRemote(bookingID, studentID, bookingRecord.campusName);
        }
        Log.addLog(campusCode, "Date: " + new Date().toLocaleString());
        Log.addLog(campusCode, "\r\nRequest Type: Book Room");
        Log.addLog(campusCode, "\r\nParameter: " + studentID + ", " + studentID);
        Log.addLog(campusCode, "\r\n" + result + "\r\n\r\n");
        return result;
    }

    public String changeReservation(String bookingID, String newCampusName, String newRoomNo, String newTimeSlot, String studentID, long orderDate) {
        String result = "";
        String date = roomManager.findRecord(bookingID).bookingDate;
        result = cancelBooking(bookingID, studentID);
        if (result.startsWith("Success")) {
            if (newCampusName.equals(campusCode)) {
                return roomManager.bookRoomLocal(newRoomNo, date, newTimeSlot, studentID, newCampusName, orderDate);
            } else {
                return roomManager.bookRoomRemote(newRoomNo, date, newTimeSlot, studentID, newCampusName, orderDate);
            }
        } else {
            return result;
        }
    }

    public void shutdown() {
        udpThread.closePort();
    }
}

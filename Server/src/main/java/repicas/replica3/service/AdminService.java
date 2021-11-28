package repicas.replica3.service;

import repicas.replica3.roommanager.RoomManager;
import repicas.replica3.roommanager.RoomManagerDVL;
import repicas.replica3.roommanager.RoomManagerKKL;
import repicas.replica3.roommanager.RoomManagerWST;
import repicas.replica3.utils.Log;

import java.util.Date;


public class AdminService extends Thread {

    RoomManager roomManager;
    private final String campusCode;

    public AdminService(String campusCode) {
        this.campusCode = campusCode;
        switch (campusCode) {
            case "DVL":
                roomManager = RoomManagerDVL.getInstance();
                break;
            case "KKL":
                roomManager = RoomManagerKKL.getInstance();
                break;
            case "WST":
                roomManager = RoomManagerWST.getInstance();
                break;
        }
    }

    public String createRoom(String roomNumber, String date, String timeSlot, String userID) {
        String result;
        if (userID.charAt(3) != 'A') {
            result = "Permission Denied!";
        } else {
            roomManager.createRoom(roomNumber, date, timeSlot);
            result = "Success!";
        }
        Log.addLog(campusCode, "Date: " + new Date().toLocaleString());
        Log.addLog(campusCode, "\r\nRequest Type: Create Room");
        Log.addLog(campusCode, "\r\nParameter: " + roomNumber + ", " + date + ", " + timeSlot + ", " + userID);
        Log.addLog(campusCode, "\r\n" + result + "\r\n\r\n");
        return result;
    }

    public String deleteRoom(String roomNumber, String date, String timeSlot, String userID) {

        String result;
        if (userID.charAt(3) != 'A') {
            result = "Permission Denied!";
        } else {
            result = roomManager.deleteRoom(roomNumber, date, timeSlot);
        }
        Log.addLog(campusCode, "Date: " + new Date().toLocaleString());
        Log.addLog(campusCode, "\r\nRequest Type: Delete Room");
        Log.addLog(campusCode, "\r\nParameter: " + roomNumber + ", " + date + ", " + timeSlot + ", " + userID);
        Log.addLog(campusCode, "\r\n" + result + "\r\n\r\n");
        return result;
    }

}

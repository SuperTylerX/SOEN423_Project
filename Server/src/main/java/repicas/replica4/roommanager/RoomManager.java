package repicas.replica4.roommanager;

import repicas.replica4.model.BookingRecord;

public interface RoomManager {

    public void createRoom(String roomNumber, String date, String timeSlot);

    public String deleteRoom(String roomNumber, String date, String timeSlot);

    public String bookRoomLocal(String roomNumber, String date, String timeSlot, String studentID, String campusName);

    public String bookRoomRemote(String roomNumber, String date, String timeSlot, String studentID, String campusName);

    public String addRoomRecord(String date, String roomNumber, String timeSlot, String studentID);

    public int getAvailableTimeSlot(String date);

    public String cancelBookingLocal(String bookingID, String studentID);

    public String cancelBookingRemote(String bookingID, String studentID, String campusName);

    public String removeRoomRecord(String date, String roomNumber, String timeSlot, String studentID);

    public String removeBookingTable(String bookingID, String studentID);

    public BookingRecord findRecord(String bookingID);


}

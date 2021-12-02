package repicas.replica2.model;

import utils.MD5Tool;

public class BookingRecord {

    public String bookingID;
    public String studentID;
    public String bookingDate;
    public String roomNumber;
    public String timeSlot;
    public long orderDate;
    public String campusName;

    public BookingRecord(String studentID, String bookingDate, String roomNumber, String timeSlot, String campusName, long orderDate) {
        this.bookingID = generateBookingID(studentID, bookingDate, roomNumber, timeSlot, campusName, orderDate);
        this.studentID = studentID;
        this.bookingDate = bookingDate;
        this.roomNumber = roomNumber;
        this.timeSlot = timeSlot;
        this.orderDate = orderDate;
        this.campusName = campusName;
    }

    public static String generateBookingID(String studentID, String bookingDate, String roomNumber, String timeSlot, String campusName, long orderDate) {
        return MD5Tool.getMD5(studentID + bookingDate + roomNumber + timeSlot + campusName + orderDate);
    }
}

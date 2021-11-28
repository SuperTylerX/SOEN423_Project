package repicas.replica3.model;

import java.util.Date;
import java.util.Random;

public class BookingRecord {

    public String bookingID;
    public String studentID;
    public String bookingDate;
    public String roomNumber;
    public String timeSlot;
    public long orderDate;
    public String campusName;

    public BookingRecord(String studentID, String bookingDate, String roomNumber, String timeSlot, String campusName) {
        this.bookingID = getRandomString(8);
        this.studentID = studentID;
        this.bookingDate = bookingDate;
        this.roomNumber = roomNumber;
        this.timeSlot = timeSlot;
        orderDate = new Date().getTime();
        this.campusName = campusName;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}

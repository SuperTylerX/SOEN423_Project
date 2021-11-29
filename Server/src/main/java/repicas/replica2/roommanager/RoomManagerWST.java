package repicas.replica2.roommanager;

import repicas.replica1.model.BookingRecord;
import repicas.replica1.utils.Network;
import repicas.replica1.Setting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManagerWST implements RoomManager {

    // Room Record data structure is a hashmap which looks like
    // ( Date => ( RoomNumber => ( TimeSlot => BookedBy )) )
    private final HashMap<String, HashMap<String, HashMap<String, String>>> roomRecords = new HashMap<>();

    private final ArrayList<BookingRecord> bookingTable = new ArrayList<>();

    public static RoomManagerWST roomManager;

    private RoomManagerWST() {
    }

    public static RoomManagerWST getInstance() {
        if (roomManager == null) {
            roomManager = new RoomManagerWST();
        }
        return roomManager;
    }

    public void createRoom(String roomNumber, String date, String timeSlot) {

        synchronized (roomRecords) {
            // if the room existed, create a new hash map
            if (roomRecords.get(date) == null) {
                HashMap<String, HashMap<String, String>> newRoom = new HashMap<>();
                roomRecords.put(date, newRoom);
            }
            if (roomRecords.get(date).get(roomNumber) == null) {
                HashMap<String, String> newTimeSlot = new HashMap<>();
                roomRecords.get(date).put(roomNumber, newTimeSlot);
            }
            roomRecords.get(date).get(roomNumber).put(timeSlot, null);
        }

    }

    public String deleteRoom(String roomNumber, String date, String timeSlot) {

        synchronized (roomRecords) {
            try {
                if (!roomRecords.get(date).get(roomNumber).containsKey(timeSlot)) {
                    return "No such record found!";
                }
                if (roomRecords.get(date).get(roomNumber).get(timeSlot) != null) {
                    // Someone reserved this room
                    String userId = roomRecords.get(date).get(roomNumber).get(timeSlot);
                    // delete it from the recordTable
                    synchronized (bookingTable) {
                        bookingTable.removeIf(bookingRecord -> bookingRecord.bookingDate.equals(date)
                                && bookingRecord.roomNumber.equals(roomNumber)
                                && bookingRecord.timeSlot.equals(timeSlot));
                    }
                }
                roomRecords.get(date).get(roomNumber).remove(timeSlot);
                return "Success!";
            } catch (NullPointerException e) {
                return "No such record found!";
            }
        }

    }

    public String bookRoomLocal(String roomNumber, String date, String timeSlot, String studentID, String campusName, long orderDate) {

        // Check if the student can book
        if (isExceedReservationLimit(studentID)) {
            // exceed reservation limit
            return "Failed! Exceed Maximum 3 Booking Limit.";
        }

        // 2. set the roomRecord to the student name
        String result = addRoomRecord(date, roomNumber, timeSlot, studentID);
        if (!result.startsWith("Success")) {
            return result;
        }

        // 3. Add the student to the record Table
        // return the reservedID
        return addBookingTable(studentID, date, roomNumber, timeSlot, campusName, orderDate);

    }

    public String bookRoomRemote(String roomNumber, String date, String timeSlot, String studentID, String campusName, long orderDate) {

        String TargetHost = "";
        int TargetPort = 0;
        switch (campusName) {
            case "DVL":
                TargetHost = Setting.DVL_HOSTNAME;
                TargetPort = Setting.DVL_UDP_SERVER_PORT;
                break;
            case "KKL":
                TargetHost = Setting.KKL_HOSTNAME;
                TargetPort = Setting.KKL_UDP_SERVER_PORT;
                break;
            case "WST":
                TargetHost = Setting.WST_HOSTNAME;
                TargetPort = Setting.WST_UDP_SERVER_PORT;
                break;
        }

        // Check if the student can book
        if (roomManager.isExceedReservationLimit(studentID)) {
            // exceed reservation limit
            return "Failed! Exceed Maximum 3 Booking Limit.";
        }

        // Call Remote server.Server to add modify the record
        String result = Network.sendUDP("bookRoomRemote\r\n"
                        .concat(roomNumber).concat("\r\n")
                        .concat(date).concat("\r\n")
                        .concat(timeSlot).concat("\r\n")
                        .concat(studentID).concat("\r\n"),
                TargetHost, TargetPort
        );

        //
        if (!result.startsWith("Success")) {
            return result;
        }

        // 3. Add the student to the record Table
        return addBookingTable(studentID, date, roomNumber, timeSlot, campusName, orderDate);

    }

    private String addBookingTable(String studentID, String date, String roomNumber, String timeSlot, String campusName, long orderDate) {
        synchronized (bookingTable) {
            BookingRecord newBookingRecord = new BookingRecord(studentID, date, roomNumber, timeSlot, campusName, orderDate);
            bookingTable.add(newBookingRecord);
            return "Success! Your Booking ID is " + newBookingRecord.bookingID;
        }
    }

    public String addRoomRecord(String date, String roomNumber, String timeSlot, String studentID) {
        synchronized (roomRecords) {
            try {
                if (!roomRecords.get(date).get(roomNumber).containsKey(timeSlot)) {
                    return "Failed! No such timeslot found!";
                }
                if (roomRecords.get(date).get(roomNumber).get(timeSlot) == null) {
                    roomRecords.get(date).get(roomNumber).put(timeSlot, studentID);
                } else {
                    // This slot is reserved by someone else
                    return "Failed! This timeslot is reserved by someone else.";
                }
            } catch (NullPointerException e) {
                return "Failed! No such timeslot found!";
            }
            return "Success!";
        }

    }

    private boolean isExceedReservationLimit(String studentID) {
        synchronized (bookingTable) {
            int count = 0;
            for (BookingRecord bookingRecord : bookingTable) {
                if (bookingRecord.studentID.equals(studentID)
                        &&
                        isThisWeek(bookingRecord.orderDate)) {
                    count++;
                    if (count >= 3) {
                        return true;
                    }
                }

            }
            return false;
        }

    }

    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    public int getAvailableTimeSlot(String date) {
        synchronized (roomRecords) {
            AtomicInteger count = new AtomicInteger();
            HashMap<String, HashMap<String, String>> thatDateRecords = roomRecords.get(date);
            if (thatDateRecords == null) {
                return count.get();
            }
            thatDateRecords.forEach((roomRecords, timeSlotRecords) -> {
                if (timeSlotRecords != null) {
                    timeSlotRecords.forEach((timeSlot, reservedBy) -> {
                        if (reservedBy == null) {
                            count.getAndIncrement();
                        }
                    });
                }
            });

            return count.get();
        }
    }

    public String cancelBookingLocal(String bookingID, String studentID) {
        synchronized (bookingTable) {
            for (BookingRecord record : bookingTable) {
                if (record.bookingID.equals(bookingID)) {
                    if (!record.studentID.equals(studentID)) {
                        return "Failed! You do not have permission to modify this booking!";
                    }
                    String result = removeRoomRecord(record.bookingDate, record.roomNumber, record.timeSlot, studentID);
                    if (!result.startsWith("Success")) {
                        return result;
                    } else {
                        break;
                    }
                }
            }
            return removeBookingTable(bookingID, studentID);
        }


    }

    public String cancelBookingRemote(String bookingID, String studentID, String campusName) {
        synchronized (bookingTable) {
            for (BookingRecord record : bookingTable) {
                if (record.bookingID.equals(bookingID)) {
                    if (!record.studentID.equals(studentID)) {
                        return "Failed! You do not have permission to modify this booking!";
                    }
                    String result = removeBookingTable(bookingID, studentID);
                    if (result.startsWith("Success")) {
                        String TargetHost = "";
                        int TargetPort = 0;
                        switch (campusName) {
                            case "DVL":
                                TargetHost = Setting.DVL_HOSTNAME;
                                TargetPort = Setting.DVL_UDP_SERVER_PORT;
                                break;
                            case "KKL":
                                TargetHost = Setting.KKL_HOSTNAME;
                                TargetPort = Setting.KKL_UDP_SERVER_PORT;
                                break;
                            case "WST":
                                TargetHost = Setting.WST_HOSTNAME;
                                TargetPort = Setting.WST_UDP_SERVER_PORT;
                                break;
                        }

                        // Call Remote server.Server to add modify the record
                        result = Network.sendUDP("removeBookingRemote\r\n"
                                        .concat(record.roomNumber).concat("\r\n")
                                        .concat(record.bookingDate).concat("\r\n")
                                        .concat(record.timeSlot).concat("\r\n")
                                        .concat(studentID).concat("\r\n"),
                                TargetHost, TargetPort
                        );

                    }
                    return result;
                }
            }

            return "Failed! No such record found!";
        }

    }

    public String removeRoomRecord(String date, String roomNumber, String timeSlot, String studentID) {
        synchronized (roomRecords) {
            try {
                if (!roomRecords.get(date).get(roomNumber).containsKey(timeSlot)) {
                    return "Failed! No such timeslot found!";
                }
                if (roomRecords.get(date).get(roomNumber).get(timeSlot).equals(studentID)) {
                    roomRecords.get(date).get(roomNumber).put(timeSlot, null);
                    return "Success!";
                } else {
                    // This slot is reserved by someone else
                    return "Failed! You do not have permission.";
                }
            } catch (NullPointerException e) {
                return "Failed! No such timeslot found!";
            }
        }

    }

    public String removeBookingTable(String bookingID, String studentID) {
        synchronized (bookingTable) {
            for (int i = 0; i < bookingTable.size(); i++) {
                BookingRecord record = bookingTable.get(i);
                if (record.bookingID.equals(bookingID)) {
                    if (!record.studentID.equals(studentID)) {
                        return "Failed! You do not have permission to modify this booking!";
                    }
                    bookingTable.remove(record);
                    return "Success!";
                }
            }
            return "Failed! Record Not Found!";
        }

    }

    public BookingRecord findRecord(String bookingID) {
        synchronized (bookingTable) {
            for (BookingRecord bookingRecord : bookingTable) {
                if (bookingRecord.bookingID.equals(bookingID)) {
                    return bookingRecord;
                }
            }
            return null;
        }
    }
}





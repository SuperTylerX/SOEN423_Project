package client;

import frontend.StudentService;
import frontend.StudentServiceImplService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StudentClient {
    public static void main(String[] args) {
        try {
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            System.out.println("Please enter your Student ID:");
            String studentID = br.readLine();
            System.out.println("Welcome! " + studentID);

            StudentServiceImplService studentServiceImplService = new StudentServiceImplService();
            StudentService studentService = studentServiceImplService.getStudentServicePort();


            label:
            while (true) {
                System.out.println("Please choose the following options:");
                System.out.println("1. Book a Room");
                System.out.println("2. Get Available Time Slot");
                System.out.println("3. Cancel a Booking");
                System.out.println("4. Change Reservation");
                System.out.println("5. Quit");

                String choice = br.readLine();
                switch (choice) {
                    case "1": {
                        System.out.println("Please enter the campus code:");
                        String campus = br.readLine();
                        System.out.println("Please enter the date(YYYY-MM-DD):");
                        String date = br.readLine();
                        System.out.println("Please enter the room number:");
                        String roomNumber = br.readLine();
                        System.out.println("Please enter the timeslot(HH:mm-HH:mm):");
                        String timeslot = br.readLine();
                        String result = studentService.bookRoom(campus, roomNumber, date, timeslot, studentID);
                        Log.addLog(studentID, "[Request] Book a room, " + campus + ", " + roomNumber + ", " + date + ", " + timeslot + "\r\n");
                        Log.addLog(studentID, "[Response] " + result + "\r\n\r\n");
                        System.out.println(result);
                        break;
                    }
                    case "2": {
                        System.out.println("Please enter the date(YYYY-MM-DD):");
                        String date = br.readLine();
                        String result = studentService.getAvailableTimeSlot(date);
                        System.out.println(result);
                        break;
                    }
                    case "3": {
                        System.out.println("Please enter the booking ID:");
                        String bookingID = br.readLine();
                        String result = studentService.cancelBooking(bookingID, studentID);
                        Log.addLog(studentID, "[Request] Cancel a booking, " + bookingID + "\r\n");
                        Log.addLog(studentID, "[Response] " + result + "\r\n\r\n");
                        System.out.println(result);
                        break;
                    }
                    case "4": {
                        System.out.println("Please enter the booking ID:");
                        String bookingID = br.readLine();
                        System.out.println("Please enter the new campus name:");
                        String campusName = br.readLine();
                        System.out.println("Please enter the new room number:");
                        String roomNumber = br.readLine();
                        System.out.println("Please enter the new time slot:");
                        String timeslot = br.readLine();
                        String result = studentService.changeReservation(bookingID, campusName, roomNumber, timeslot, studentID);
                        Log.addLog(studentID, "[Request] Change Reservation, " + bookingID + ", " + campusName + ", " + roomNumber + ", " + timeslot + "\r\n");
                        Log.addLog(studentID, "[Response] " + result + "\r\n\r\n");
                        System.out.println(result);
                        break;
                    }
                    case "5":
                        break label;
                    default:
                        System.out.println("Invalid Input");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
    }
}

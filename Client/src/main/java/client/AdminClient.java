package client;

import frontend.AdminService;
import frontend.AdminServiceImplService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AdminClient {
    public static void main(String[] args) {
        try {
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            System.out.println("Please enter your Admin ID:");
            String adminID = br.readLine();
            System.out.println("Welcome! " + adminID);

            AdminServiceImplService adminServiceImplService = new AdminServiceImplService();
            AdminService adminService = adminServiceImplService.getAdminServicePort();


            label:
            while (true) {
                System.out.println("Please choose the following options:");
                System.out.println("1. Create Room");
                System.out.println("2. Remove Room");
                System.out.println("3. Quit");

                String choice = br.readLine();
                switch (choice) {
                    case "1": {
                        System.out.println("Please enter the room:");
                        String room = br.readLine();
                        System.out.println("Please enter the date(YYYY-MM-DD):");
                        String date = br.readLine();
                        System.out.println("Please enter the timeslot(HH:mm-HH:mm):");
                        String timeslot = br.readLine();
                        String result = adminService.createRoom(room, date, timeslot, adminID);
                        Log.addLog(adminID, "[Request] Create a room, " + room + ", " + date + ", " + timeslot + "\r\n");
                        Log.addLog(adminID, "[Response] " + result + "\r\n\r\n");
                        System.out.println(result);
                        break;
                    }
                    case "2": {
                        System.out.println("Please enter the room:");
                        String room = br.readLine();
                        System.out.println("Please enter the date(YYYY-MM-DD):");
                        String date = br.readLine();
                        System.out.println("Please enter the timeslot(HH:mm-HH:mm):");
                        String timeslot = br.readLine();
                        String result = adminService.deleteRoom(room, date, timeslot, adminID);
                        Log.addLog(adminID, "[Request] Delete a room, " + room + ", " + date + ", " + timeslot + "\r\n");
                        Log.addLog(adminID, "[Response] " + result + "\r\n\r\n");
                        System.out.println(result);
                        break;
                    }
                    case "3":
                        break label;
                    default:
                        System.out.println("Invalid Input");
                        break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


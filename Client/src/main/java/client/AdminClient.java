package client;

import frontend.AdminService;
import frontend.AdminServiceImplService;

public class AdminClient {
    public static void main(String[] args) {
        AdminServiceImplService adminServiceImplService = new AdminServiceImplService();
        AdminService adminService = adminServiceImplService.getAdminServicePort();

        String response1 = adminService.createRoom("101","2021-11-27","9:00-10:00", "DVLA1000"); // attach a requestID
        String response2 = adminService.createRoom("102","2021-11-27","9:00-10:00", "DVLA1000"); // attach a requestID

        System.out.println("response " + response1);
        System.out.println("response " + response2);



    }
}

package client;

import frontend.AdminService;
import frontend.AdminServiceImplService;

public class AdminClient {
    public static void main(String[] args) {
        AdminServiceImplService adminServiceImplService = new AdminServiceImplService();
        AdminService adminService = adminServiceImplService.getAdminServicePort();

        String response1 = adminService.createRoom("101", "2021-11-27", "9:00-10:00", "DVLA1000");
        System.out.println("response1 " + response1);
        String response2 = adminService.deleteRoom("101", "2021-11-27", "10:00-11:00", "DVLA1000");
        System.out.println("response2 " + response2);


    }
}

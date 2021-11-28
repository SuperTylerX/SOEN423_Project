package client;

import frontend.AdminService;
import frontend.AdminServiceImplService;

public class AdminClient {
    public static void main(String[] args) {
        AdminServiceImplService adminServiceImplService = new AdminServiceImplService();
        AdminService adminService = adminServiceImplService.getAdminServicePort();

        adminService.createRoom("101","2021-11-27","9:00-10:00", "DVLA1000");
    }
}

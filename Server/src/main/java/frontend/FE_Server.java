package frontend;

import javax.xml.ws.Endpoint;

import static common.Setting.FRONTEND_PORT;

public class FE_Server {
    public static void main(String[] args) {

        ResponseWaitingList.initialize_error_count();

        Listener listener = new Listener();
        listener.start();

        String adminServicePath = "/AdminService";
        String studentServicePath = "/StudentService";
        String BASE_URI = "http://localhost:" + FRONTEND_PORT;

        AdminServiceImpl adminService = new AdminServiceImpl();
        StudentServiceImpl studentService = new StudentServiceImpl();

        Endpoint.publish(BASE_URI + adminServicePath, adminService);
        Endpoint.publish(BASE_URI + studentServicePath, studentService);

        System.out.println("SOAP Service listening on " + BASE_URI + adminServicePath + "?wsdl");
        System.out.println("SOAP Service listening on " + BASE_URI + studentServicePath + "?wsdl");
    }


}

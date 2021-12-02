package frontend;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface AdminService {
    @WebMethod
    String createRoom(@WebParam(name = "roomNumber") String roomNumber, @WebParam(name = "date") String date, @WebParam(name = "timeSlot") String timeSlot, @WebParam(name = "userID") String userID);

    @WebMethod
    String deleteRoom(@WebParam(name = "roomNumber") String roomNumber, @WebParam(name = "date") String date, @WebParam(name = "timeSlot") String timeSlot, @WebParam(name = "userID") String userID);

}


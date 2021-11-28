package frontend;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface StudentService {

    @WebMethod
    String bookRoom(@WebParam(name = "campusName") String campusName,
                    @WebParam(name = "roomNumber") String roomNumber,
                    @WebParam(name = "date") String date,
                    @WebParam(name = "timeSlot") String timeSlot,
                    @WebParam(name = "studentID") String studentID);

    @WebMethod
    String getAvailableTimeSlot(@WebParam(name = "date") String date);

    @WebMethod
    String cancelBooking(@WebParam(name = "bookingID") String bookingID,
                         @WebParam(name = "studentID") String studentID);

    @WebMethod
    String changeReservation(@WebParam(name = "bookingID") String bookingID,
                             @WebParam(name = "newCampusName") String newCampusName,
                             @WebParam(name = "newRoomNo") String newRoomNo,
                             @WebParam(name = "newTimeSlot") String newTimeSlot,
                             @WebParam(name = "studentID") String studentID);
}

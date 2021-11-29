package frontend;

import packet.Packet;
import packet.parameter.*;

import javax.jws.WebService;
import java.util.Date;

@WebService(name = "StudentService", endpointInterface = "frontend.StudentService")
public class StudentServiceImpl implements StudentService {

    @Override
    public String bookRoom(String campusName, String roomNumber, String date, String timeSlot, String studentID) {
        OperationParameter op = new BookRoomParameter(campusName, roomNumber, date, timeSlot, studentID, new Date().getTime());
        Packet request = new Packet(Operation.BOOK_ROOM, op, studentID.substring(0, 3));
        return SFT_HA.make_SFT_HA(request);
    }

    @Override
    public String getAvailableTimeSlot(String date) {
        OperationParameter op = new GetAvailableTimeSlotParameter(date);
        Packet request = new Packet(Operation.GET_AVAILABLE_TIME_SLOT, op, "DVL");  // TODO: ask Tian
        return SFT_HA.make_SFT_HA(request);
    }

    @Override
    public String cancelBooking(String bookingID, String studentID) {
        OperationParameter op = new CancelBookingParameter(bookingID, studentID);
        Packet request = new Packet(Operation.CANCEL_BOOKING, op, studentID.substring(0, 3));  // TODO: ask Tian
        return SFT_HA.make_SFT_HA(request);
    }

    @Override
    public String changeReservation(String bookingID, String newCampusName, String newRoomNo, String newTimeSlot, String studentID) {
        OperationParameter op = new ChangeReservationParameter(bookingID, newCampusName, newRoomNo, newTimeSlot, studentID, new Date().getTime());
        Packet request = new Packet(Operation.CHANGE_RESERVATION, op, studentID.substring(0, 3));
        return SFT_HA.make_SFT_HA(request);
    }
}

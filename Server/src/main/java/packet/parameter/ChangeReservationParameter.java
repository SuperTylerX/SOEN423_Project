package packet.parameter;

public class ChangeReservationParameter extends OperationParameter{
    String bookingID;
    String newCampusName;
    String newRoomNo;
    String newTimeSlot;
    String studentID;

    public ChangeReservationParameter(String bookingID, String newCampusName, String newRoomNo, String newTimeSlot, String studentID) {
        this.bookingID = bookingID;
        this.newCampusName = newCampusName;
        this.newRoomNo = newRoomNo;
        this.newTimeSlot = newTimeSlot;
        this.studentID = studentID;
    }
}

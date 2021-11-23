package packet.parameter;

public class ChangeReservationParameter extends OperationParameter{
    public String bookingID;
    public String newCampusName;
    public String newRoomNo;
    public String newTimeSlot;
    public String studentID;

    public ChangeReservationParameter(String bookingID, String newCampusName, String newRoomNo, String newTimeSlot, String studentID) {
        this.bookingID = bookingID;
        this.newCampusName = newCampusName;
        this.newRoomNo = newRoomNo;
        this.newTimeSlot = newTimeSlot;
        this.studentID = studentID;
    }
}

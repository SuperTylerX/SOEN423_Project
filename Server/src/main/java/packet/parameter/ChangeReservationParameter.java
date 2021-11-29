package packet.parameter;

public class ChangeReservationParameter extends OperationParameter {
    public String bookingID;
    public String newCampusName;
    public String newRoomNo;
    public String newTimeSlot;
    public String studentID;
    public long orderDate;

    public ChangeReservationParameter(String bookingID, String newCampusName, String newRoomNo, String newTimeSlot, String studentID, long orderDate) {
        this.bookingID = bookingID;
        this.newCampusName = newCampusName;
        this.newRoomNo = newRoomNo;
        this.newTimeSlot = newTimeSlot;
        this.studentID = studentID;
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "ChangeReservationParameter{" +
                "bookingID='" + bookingID + '\'' +
                ", newCampusName='" + newCampusName + '\'' +
                ", newRoomNo='" + newRoomNo + '\'' +
                ", newTimeSlot='" + newTimeSlot + '\'' +
                ", studentID='" + studentID + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}

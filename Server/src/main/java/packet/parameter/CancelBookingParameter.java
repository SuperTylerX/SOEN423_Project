package packet.parameter;

public class CancelBookingParameter extends OperationParameter {

    public String bookingID;
    public String studentID;

    public CancelBookingParameter(String bookingID, String studentID) {
        this.bookingID = bookingID;
        this.studentID = studentID;
    }
}

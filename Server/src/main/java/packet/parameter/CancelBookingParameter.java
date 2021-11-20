package packet.parameter;

public class CancelBookingParameter extends OperationParameter {

    String bookingID;
    String studentID;

    public CancelBookingParameter(String bookingID, String studentID) {
        this.bookingID = bookingID;
        this.studentID = studentID;
    }
}

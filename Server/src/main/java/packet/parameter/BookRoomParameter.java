package packet.parameter;

public class BookRoomParameter extends OperationParameter {

    public String campusName;
    public String roomNumber;
    public String date;
    public String timeSlot;
    public String studentID;

    public BookRoomParameter(String campusName, String roomNumber, String date, String timeSlot, String studentID) {
        this.campusName = campusName;
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.studentID = studentID;
    }
}

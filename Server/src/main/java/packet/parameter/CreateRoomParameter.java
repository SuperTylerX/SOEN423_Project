package packet.parameter;

public class CreateRoomParameter extends OperationParameter {

    public String roomNumber;
    public String date;
    public String timeSlot;
    public String userID;

    public CreateRoomParameter(String roomNumber, String date, String timeSlot, String userID) {
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.userID = userID;
    }
}

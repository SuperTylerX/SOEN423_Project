package packet.parameter;

public class CreateRoomParameter extends OperationParameter {

    String roomNumber;
    String date;
    String timeSlot;
    String userID;

    public CreateRoomParameter(String roomNumber, String date, String timeSlot, String userID) {
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.userID = userID;
    }
}

package packet.parameter;

public class DeleteRoomParameter extends OperationParameter {

    String roomNumber;
    String date;
    String timeSlot;
    String userID;

    public DeleteRoomParameter(String roomNumber, String date, String timeSlot, String userID) {
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.userID = userID;
    }
}

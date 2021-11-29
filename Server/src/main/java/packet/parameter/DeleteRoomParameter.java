package packet.parameter;

public class DeleteRoomParameter extends OperationParameter {

    public String roomNumber;
    public String date;
    public String timeSlot;
    public String userID;

    public DeleteRoomParameter(String roomNumber, String date, String timeSlot, String userID) {
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "DeleteRoomParameter{" +
                "roomNumber='" + roomNumber + '\'' +
                ", date='" + date + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}

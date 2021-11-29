package packet.parameter;

public class BookRoomParameter extends OperationParameter {

    public String campusName;
    public String roomNumber;
    public String date;
    public String timeSlot;
    public String studentID;
    public long orderDate;

    public BookRoomParameter(String campusName, String roomNumber, String date, String timeSlot, String studentID, long orderDate) {
        this.campusName = campusName;
        this.roomNumber = roomNumber;
        this.date = date;
        this.timeSlot = timeSlot;
        this.studentID = studentID;
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "BookRoomParameter{" +
                "campusName='" + campusName + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", date='" + date + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", studentID='" + studentID + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}

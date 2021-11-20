package packet.parameter;

public class GetAvailableTimeSlotParameter extends OperationParameter {

    String date;

    public GetAvailableTimeSlotParameter(String date) {
        this.date = date;
    }
}

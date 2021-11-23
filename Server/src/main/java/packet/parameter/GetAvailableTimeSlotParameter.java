package packet.parameter;

public class GetAvailableTimeSlotParameter extends OperationParameter {

    public String date;

    public GetAvailableTimeSlotParameter(String date) {
        this.date = date;
    }
}

package replicamanger;

import packet.parameter.GetAvailableTimeSlotParameter;
import packet.parameter.OperationParameter;

public class OperationAdapterFactory {
    static public RuntimeTypeAdapterFactory<OperationParameter> operationAdapterFactory = RuntimeTypeAdapterFactory.of(OperationParameter.class, "type")
            .registerSubtype(GetAvailableTimeSlotParameter.class, "GetAvailableTimeSlotParameter");
}

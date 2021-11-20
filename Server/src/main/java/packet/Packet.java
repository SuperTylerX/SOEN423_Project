package packet;

import packet.parameter.Operation;
import packet.parameter.OperationParameter;

import java.io.Serializable;

public class Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    private int sequenceNumber;
    private Operation operation;
    private OperationParameter operationParameter;
    private String campus;

    public Packet(int sequenceNumber, Operation operation, OperationParameter operationParameter, String campus) {
        this.sequenceNumber = sequenceNumber;
        this.operation = operation;
        this.operationParameter = operationParameter;
        this.campus = campus;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Operation getOperation() {
        return operation;
    }

    public OperationParameter getOperationParameter() {
        return operationParameter;
    }

    public String getCampus() {
        return campus;
    }

    @Override
    public String toString() {
        return "SequencerPacket{" +
                "sequenceNumber=" + sequenceNumber +
                ", operation=" + operation +
                ", operationParameter=" + operationParameter +
                '}';
    }
}

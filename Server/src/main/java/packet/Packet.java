package packet;

import packet.parameter.Operation;
import packet.parameter.OperationParameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    private String identifier;
    private int sequenceNumber;
    private Operation operation;
    private OperationParameter operationParameter;
    private String campus;
    public CopyOnWriteArrayList<Integer> ACKs = new CopyOnWriteArrayList<>();

    public Packet() {
    }

    // constructor used for FE. maybe add identifier to the parameter later...
    public Packet(Operation operation, OperationParameter operationParameter, String campus) {
        this.operation = operation;
        this.operationParameter = operationParameter;
        this.campus = campus;
    }

    // constructor used for sequencer. will be deleted laterl...
    public Packet(int sequenceNumber, Operation operation, OperationParameter operationParameter, String campus) {
        this.sequenceNumber = sequenceNumber;
        this.operation = operation;
        this.operationParameter = operationParameter;
        this.campus = campus;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

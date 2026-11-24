package dev.shipflow.shipment.application;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(String trackingNumber) {
        super("Shipment " + trackingNumber + " not found");
    }
}

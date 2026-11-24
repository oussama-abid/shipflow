package dev.shipflow.shipment.api;

import dev.shipflow.shipment.domain.Shipment;
import dev.shipflow.shipment.domain.ShipmentStatus;

import java.time.Instant;

public record ShipmentResponse(
    String trackingNumber,
    String sender,
    String recipient,
    String destinationAddress,
    ShipmentStatus status,
    Instant createdAt,
    Instant updatedAt,
    long version
) {

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
            shipment.getTrackingNumber(),
            shipment.getSender(),
            shipment.getRecipient(),
            shipment.getDestinationAddress(),
            shipment.getStatus(),
            shipment.getCreatedAt(),
            shipment.getUpdatedAt(),
            shipment.getVersion()
        );
    }
}

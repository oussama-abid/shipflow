package dev.shipflow.shipment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Shipment {

    private final UUID id;
    private final String sender;
    private final String recipient;
    private final String destinationAddress;
    private final Instant createdAt;
    private ShipmentStatus status;
    private Instant updatedAt;

    public Shipment(String sender, String recipient, String destinationAddress) {
        this.id = UUID.randomUUID();
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
        this.destinationAddress = Objects.requireNonNull(destinationAddress, "destinationAddress must not be null");
        this.status = ShipmentStatus.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void changeStatus(ShipmentStatus next) {
        Objects.requireNonNull(next, "next status must not be null");
        if (status.isFinal()) {
            throw new InvalidStatusTransitionException("Shipment " + id + " is already " + status + " and cannot be changed");
        }
        if (!status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException("Cannot change status of shipment " + id + " from " + status + " to " + next);
        }
        this.status = next;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

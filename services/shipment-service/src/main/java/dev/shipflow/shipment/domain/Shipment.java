package dev.shipflow.shipment.domain;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment {

    private static final String TRACKING_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TRACKING_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    private UUID id;
    private String trackingNumber;
    private String sender;
    private String recipient;
    private String destinationAddress;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    @Version
    private long version;

    protected Shipment() {
    }

    public Shipment(String sender, String recipient, String destinationAddress) {
        this.id = UUID.randomUUID();
        this.trackingNumber = generateTrackingNumber();
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
        this.destinationAddress = Objects.requireNonNull(destinationAddress, "destinationAddress must not be null");
        this.status = ShipmentStatus.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    private static String generateTrackingNumber() {
        StringBuilder result = new StringBuilder("SF-");
        for (int i = 0; i < TRACKING_LENGTH; i++) {
            result.append(TRACKING_CHARS.charAt(RANDOM.nextInt(TRACKING_CHARS.length())));
        }
        return result.toString();
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

    public String getTrackingNumber() {
        return trackingNumber;
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

    public long getVersion() {
        return version;
    }
}

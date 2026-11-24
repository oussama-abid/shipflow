package dev.shipflow.shipment.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class ShipmentTest {

    private Shipment newShipment() {
        return new Shipment("Alice", "Bob", "Hauptstr. 1, Bremen");
    }

    @Test
    void newShipmentStartsAsCreated() {
        Shipment shipment = newShipment();

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.CREATED);
    }

    @Test
    void newShipmentHasGeneratedId() {
        Shipment first = newShipment();
        Shipment second = newShipment();

        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isNotNull();
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void newShipmentKeepsGivenData() {
        Shipment shipment = newShipment();

        assertThat(shipment.getSender()).isEqualTo("Alice");
        assertThat(shipment.getRecipient()).isEqualTo("Bob");
        assertThat(shipment.getDestinationAddress()).isEqualTo("Hauptstr. 1, Bremen");
    }

    @Test
    void newShipmentHasEqualTimestamps() {
        Shipment shipment = newShipment();

        assertThat(shipment.getCreatedAt()).isNotNull();
        assertThat(shipment.getUpdatedAt()).isEqualTo(shipment.getCreatedAt());
    }

    @Test
    void constructorRejectsNullValues() {
        assertThatNullPointerException()
            .isThrownBy(() -> new Shipment(null, "Bob", "Hauptstr. 1, Bremen"))
            .withMessageContaining("sender");
        assertThatNullPointerException()
            .isThrownBy(() -> new Shipment("Alice", null, "Hauptstr. 1, Bremen"))
            .withMessageContaining("recipient");
        assertThatNullPointerException()
            .isThrownBy(() -> new Shipment("Alice", "Bob", null))
            .withMessageContaining("destinationAddress");
    }

    @Test
    void validChangeUpdatesStatus() {
        Shipment shipment = newShipment();

        shipment.changeStatus(ShipmentStatus.PICKED_UP);

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PICKED_UP);
    }

    @Test
    void validChangeUpdatesTimestamp() {
        Shipment shipment = newShipment();
        Instant createdAt = shipment.getCreatedAt();

        shipment.changeStatus(ShipmentStatus.PICKED_UP);

        assertThat(shipment.getUpdatedAt()).isAfterOrEqualTo(createdAt);
        assertThat(shipment.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void forbiddenChangeThrowsAndKeepsStatus() {
        Shipment shipment = newShipment();

        assertThatThrownBy(() -> shipment.changeStatus(ShipmentStatus.DELIVERED))
            .isInstanceOf(InvalidStatusTransitionException.class)
            .hasMessageContaining("from CREATED to DELIVERED");

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.CREATED);
    }

    @Test
    void changeFromFinalStatusThrows() {
        Shipment shipment = newShipment();
        shipment.changeStatus(ShipmentStatus.PICKED_UP);
        shipment.changeStatus(ShipmentStatus.IN_TRANSIT);
        shipment.changeStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        shipment.changeStatus(ShipmentStatus.DELIVERED);

        assertThatThrownBy(() -> shipment.changeStatus(ShipmentStatus.RETURNED))
            .isInstanceOf(InvalidStatusTransitionException.class)
            .hasMessageContaining("already DELIVERED");

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    void changeToNullThrows() {
        Shipment shipment = newShipment();

        assertThatNullPointerException()
            .isThrownBy(() -> shipment.changeStatus(null));

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.CREATED);
    }
}

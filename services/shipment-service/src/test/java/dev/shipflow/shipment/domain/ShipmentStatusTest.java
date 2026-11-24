package dev.shipflow.shipment.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ShipmentStatusTest {

    @ParameterizedTest
    @CsvSource({
        "CREATED, PICKED_UP",
        "CREATED, CANCELLED",
        "PICKED_UP, IN_TRANSIT",
        "IN_TRANSIT, OUT_FOR_DELIVERY",
        "OUT_FOR_DELIVERY, DELIVERED",
        "OUT_FOR_DELIVERY, DELIVERY_FAILED",
        "DELIVERY_FAILED, OUT_FOR_DELIVERY",
        "DELIVERY_FAILED, RETURNED"
    })
    void allowsValidTransitions(ShipmentStatus from, ShipmentStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "CREATED, DELIVERED",
        "CREATED, IN_TRANSIT",
        "PICKED_UP, DELIVERED",
        "PICKED_UP, CANCELLED",
        "IN_TRANSIT, PICKED_UP",
        "OUT_FOR_DELIVERY, IN_TRANSIT",
        "DELIVERY_FAILED, DELIVERED",
        "CREATED, CREATED"
    })
    void rejectsInvalidTransitions(ShipmentStatus from, ShipmentStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ShipmentStatus.class, names = {"DELIVERED", "RETURNED", "CANCELLED"})
    void finalStatusesCannotTransitionAnywhere(ShipmentStatus status) {
        assertThat(status.isFinal()).isTrue();
        for (ShipmentStatus next : ShipmentStatus.values()) {
            assertThat(status.canTransitionTo(next)).isFalse();
        }
    }

    @ParameterizedTest
    @EnumSource(value = ShipmentStatus.class, names = {"DELIVERED", "RETURNED", "CANCELLED"}, mode = EnumSource.Mode.EXCLUDE)
    void activeStatusesAreNotFinal(ShipmentStatus status) {
        assertThat(status.isFinal()).isFalse();
    }
}

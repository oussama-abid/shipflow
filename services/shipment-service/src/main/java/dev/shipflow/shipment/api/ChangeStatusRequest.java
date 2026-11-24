package dev.shipflow.shipment.api;

import dev.shipflow.shipment.domain.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
    @NotNull ShipmentStatus status,
    @NotNull Long expectedVersion
) {
}

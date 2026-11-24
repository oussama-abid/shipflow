package dev.shipflow.shipment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShipmentRequest(
    @NotBlank @Size(max = 255) String sender,
    @NotBlank @Size(max = 255) String recipient,
    @NotBlank @Size(max = 500) String destinationAddress
) {
}

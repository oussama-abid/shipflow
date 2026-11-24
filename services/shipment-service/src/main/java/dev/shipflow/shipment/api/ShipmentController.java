package dev.shipflow.shipment.api;

import dev.shipflow.shipment.application.ShipmentService;
import dev.shipflow.shipment.domain.Shipment;
import dev.shipflow.shipment.domain.ShipmentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        Shipment shipment = shipmentService.createShipment(
            request.sender(), request.recipient(), request.destinationAddress());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{trackingNumber}")
            .buildAndExpand(shipment.getTrackingNumber())
            .toUri();
        return ResponseEntity.created(location).body(ShipmentResponse.from(shipment));
    }

    @GetMapping("/{trackingNumber}")
    public ShipmentResponse getShipment(@PathVariable String trackingNumber) {
        return ShipmentResponse.from(shipmentService.getShipment(trackingNumber));
    }

    @GetMapping
    public PagedModel<ShipmentResponse> listShipments(
        @RequestParam(required = false) ShipmentStatus status,
        @PageableDefault(size = 20) Pageable pageable) {
        return new PagedModel<>(shipmentService.listShipments(status, pageable).map(ShipmentResponse::from));
    }

    @PostMapping("/{trackingNumber}/status")
    public ShipmentResponse changeStatus(@PathVariable String trackingNumber,
                                         @Valid @RequestBody ChangeStatusRequest request) {
        return ShipmentResponse.from(
            shipmentService.changeStatus(trackingNumber, request.status(), request.expectedVersion()));
    }
}

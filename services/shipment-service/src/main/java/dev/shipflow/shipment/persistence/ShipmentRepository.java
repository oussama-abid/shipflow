package dev.shipflow.shipment.persistence;

import dev.shipflow.shipment.domain.Shipment;
import dev.shipflow.shipment.domain.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

}

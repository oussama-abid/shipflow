package dev.shipflow.shipment.application;

import dev.shipflow.shipment.domain.Shipment;
import dev.shipflow.shipment.domain.ShipmentStatus;
import dev.shipflow.shipment.persistence.ShipmentRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;

    public ShipmentService(ShipmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Shipment createShipment(String sender, String recipient, String destinationAddress) {
        return repository.save(new Shipment(sender, recipient, destinationAddress));
    }

    @Transactional(readOnly = true)
    public Shipment getShipment(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new ShipmentNotFoundException(trackingNumber));
    }

    @Transactional(readOnly = true)
    public Page<Shipment> listShipments(ShipmentStatus status, Pageable pageable) {
        if (status == null) {
            return repository.findAll(pageable);
        }
        return repository.findByStatus(status, pageable);
    }

    @Transactional
    public Shipment changeStatus(String trackingNumber, ShipmentStatus next, long expectedVersion) {
        Shipment shipment = getShipment(trackingNumber);
        if (shipment.getVersion() != expectedVersion) {
            throw new OptimisticLockingFailureException(
                "Shipment " + trackingNumber + " was modified: expected version "
                    + expectedVersion + " but current version is " + shipment.getVersion());
        }
        shipment.changeStatus(next);
        return repository.saveAndFlush(shipment);
    }
}

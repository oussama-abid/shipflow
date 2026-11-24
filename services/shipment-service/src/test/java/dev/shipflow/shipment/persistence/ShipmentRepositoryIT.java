package dev.shipflow.shipment.persistence;

import dev.shipflow.shipment.TestcontainersConfiguration;
import dev.shipflow.shipment.domain.Shipment;
import dev.shipflow.shipment.domain.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ShipmentRepositoryIT {

    @Autowired
    private ShipmentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private Shipment saveNewShipment() {
        return repository.save(new Shipment("Alice", "Bob", "Hauptstr. 1, Bremen"));
    }

    @Test
    void savesAndLoadsShipment() {
        Shipment saved = saveNewShipment();

        Shipment loaded = repository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getId()).isEqualTo(saved.getId());
        assertThat(loaded.getTrackingNumber()).isEqualTo(saved.getTrackingNumber());
        assertThat(loaded.getSender()).isEqualTo("Alice");
        assertThat(loaded.getStatus()).isEqualTo(ShipmentStatus.CREATED);
    }

    @Test
    void findsShipmentByTrackingNumber() {
        Shipment saved = saveNewShipment();

        Optional<Shipment> found = repository.findByTrackingNumber(saved.getTrackingNumber());
        Optional<Shipment> unknown = repository.findByTrackingNumber("SF-UNKNOWN1");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(unknown).isEmpty();
    }

    @Test
    void persistsStatusChangeAndIncrementsVersion() {
        Shipment saved = saveNewShipment();
        long initialVersion = saved.getVersion();

        saved.changeStatus(ShipmentStatus.PICKED_UP);
        repository.save(saved);

        Shipment loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(ShipmentStatus.PICKED_UP);
        assertThat(loaded.getVersion()).isGreaterThan(initialVersion);
    }

    @Test
    void rejectsConcurrentUpdateOfSameShipment() {
        Shipment saved = saveNewShipment();
        Shipment copyA = repository.findById(saved.getId()).orElseThrow();
        Shipment copyB = repository.findById(saved.getId()).orElseThrow();

        copyA.changeStatus(ShipmentStatus.PICKED_UP);
        repository.save(copyA);

        copyB.changeStatus(ShipmentStatus.CANCELLED);
        assertThatThrownBy(() -> repository.save(copyB))
            .isInstanceOf(OptimisticLockingFailureException.class);

        Shipment loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(ShipmentStatus.PICKED_UP);
    }
}

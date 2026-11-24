package dev.shipflow.shipment.domain;

import java.util.Set;

public enum ShipmentStatus {
    CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, DELIVERY_FAILED, CANCELLED, RETURNED;


    public boolean canTransitionTo(ShipmentStatus next) {
        return switch (this) {
            case CREATED -> Set.of(PICKED_UP, CANCELLED).contains(next);
            case PICKED_UP -> Set.of(IN_TRANSIT).contains(next);
            case IN_TRANSIT -> Set.of(OUT_FOR_DELIVERY).contains(next);
            case OUT_FOR_DELIVERY -> Set.of(DELIVERED, DELIVERY_FAILED).contains(next);
            case DELIVERY_FAILED -> Set.of(OUT_FOR_DELIVERY, RETURNED).contains(next);
            case DELIVERED, CANCELLED, RETURNED -> false;

        };
    }

    public boolean isFinal() {
        for (ShipmentStatus s : ShipmentStatus.values()) {
            if (canTransitionTo(s)) {
                return false;
            }
        }
        return true;
    }
}

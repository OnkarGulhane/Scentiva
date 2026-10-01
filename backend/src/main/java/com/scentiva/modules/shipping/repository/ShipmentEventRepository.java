package com.scentiva.modules.shipping.repository;

import com.scentiva.modules.shipping.model.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, Long> {

    List<ShipmentEvent> findByShipmentIdOrderByEventTimestampAsc(Long shipmentId);
}

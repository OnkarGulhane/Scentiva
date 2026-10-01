package com.scentiva.modules.shipping.repository;

import com.scentiva.modules.shipping.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    @Query("SELECT s FROM Shipment s LEFT JOIN FETCH s.events WHERE s.order.id = :orderId AND s.isDeleted = false")
    Optional<Shipment> findByOrderIdWithEvents(@Param("orderId") Long orderId);

    Optional<Shipment> findByOrderIdAndIsDeletedFalse(Long orderId);

    @Query("SELECT s FROM Shipment s LEFT JOIN FETCH s.events WHERE s.trackingNumber = :trackingNumber AND s.isDeleted = false")
    Optional<Shipment> findByTrackingNumberWithEvents(@Param("trackingNumber") String trackingNumber);

    Optional<Shipment> findByTrackingNumberAndIsDeletedFalse(String trackingNumber);
}

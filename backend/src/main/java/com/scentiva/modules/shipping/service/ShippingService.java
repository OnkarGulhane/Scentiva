package com.scentiva.modules.shipping.service;

import com.scentiva.modules.shipping.dto.*;

public interface ShippingService {

    ShipmentResponse createShipment(ShipmentCreateRequest request);

    ShipmentResponse updateShipmentStatus(Long shipmentId, ShipmentStatusUpdateRequest request);

    ShipmentResponse getShipmentByOrderId(Long orderId);

    ShipmentResponse getShipmentByTrackingNumber(String trackingNumber);

    ShipmentTrackingResponse trackShipment(String trackingNumber);
}

package com.scentiva.modules.shipping.provider;

import com.scentiva.modules.shipping.model.ShippingProviderType;

public interface ShippingProvider {

    ShippingProviderType getProviderType();

    ShipmentCreateResult createShipment(ShipmentCreateCommand command);

    boolean cancelShipment(String trackingNumber);
}

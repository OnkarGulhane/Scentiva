package com.scentiva.modules.admin.dto;

import com.scentiva.modules.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCountDto {
    private OrderStatus status;
    private long count;
}

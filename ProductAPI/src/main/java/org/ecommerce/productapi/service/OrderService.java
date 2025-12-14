package org.ecommerce.productapi.service;

import org.ecommerce.productapi.domain.Order;
import org.ecommerce.productapi.domain.OrderItem;
import graphql.schema.DataFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order getOrderById(Long orderId);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    
    Page<Order> getAllOrders(Pageable pageable);

    Page<Order> getUserOrders(String email, Pageable pageable);

    Order postOrder(Order validOrder, Map<Long, Long> perfumesId);

    String deleteOrder(Long orderId);

    DataFetcher<List<Order>> getAllOrdersByQuery();

    DataFetcher<List<Order>> getUserOrdersByEmailQuery();
}

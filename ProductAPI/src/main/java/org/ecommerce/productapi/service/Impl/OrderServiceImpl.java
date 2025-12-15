package org.ecommerce.productapi.service.Impl;

import org.ecommerce.productapi.domain.Order;
import org.ecommerce.productapi.domain.OrderItem;
import org.ecommerce.productapi.domain.Perfume;
import org.ecommerce.productapi.dto.MailRequest;
import org.ecommerce.productapi.exception.ApiRequestException;
import org.ecommerce.productapi.repository.OrderItemRepository;
import org.ecommerce.productapi.repository.OrderRepository;
import org.ecommerce.productapi.repository.PerfumeRepository;
import org.ecommerce.productapi.security.JwtProvider;
import org.ecommerce.productapi.service.OrderService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ecommerce.productapi.constants.ErrorMessage.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PerfumeRepository perfumeRepository;
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;

    private final String notificationApiUrl = "http://spring-app-notification:8091/api/v1/notification/mail";


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByIdAsc(pageable);
    }

    @Override
    public Page<Order> getUserOrders(String email, Pageable pageable) {
        return orderRepository.findOrderByEmail(email, pageable);
    }

    @Override
    @Transactional
    public Order postOrder(Order order, Map<Long, Long> perfumesId) {
        List<OrderItem> orderItemList = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : perfumesId.entrySet()) {
            Perfume perfume = perfumeRepository.findById(entry.getKey()).get();
            OrderItem orderItem = new OrderItem();
            orderItem.setPerfume(perfume);
            orderItem.setAmount((perfume.getPrice() * entry.getValue()));
            orderItem.setQuantity(entry.getValue());
            orderItemList.add(orderItem);
            orderItemRepository.save(orderItem);
        }
        order.getOrderItems().addAll(orderItemList);
        orderRepository.save(order);

        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(order.getEmail());
        mailRequest.setSubject("Order #" + order.getId());
        mailRequest.setTemplate("order-template");
        mailRequest.setAttributes(Map.of("order", order));

        String token = jwtProvider.createMailToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // sets Authorization: Bearer <token>
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MailRequest> entity = new HttpEntity<>(mailRequest, headers);

        // Send POST request
        restTemplate.postForEntity(notificationApiUrl, entity, Void.class);

        return order;
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        return "Order deleted successfully";
    }

    @Override
    public DataFetcher<List<Order>> getAllOrdersByQuery() {
        return dataFetchingEnvironment -> orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByEmailQuery() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email").toString();
            return orderRepository.findOrderByEmail(email);
        };
    }
}

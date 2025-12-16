package org.ecommerce.productapi.service.Impl;

import org.ecommerce.productapi.domain.Order;
import org.ecommerce.productapi.domain.OrderItem;
import org.ecommerce.productapi.domain.Perfume;
import org.ecommerce.productapi.dto.MailRequest;
import org.ecommerce.productapi.dto.order.OrderItemMailDto;
import org.ecommerce.productapi.dto.order.OrderMailDto;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ecommerce.productapi.constants.ErrorMessage.MAIL_NOT_SENT;
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

    public OrderMailDto mapToMailDto(Order order) {
        OrderMailDto dto = new OrderMailDto();
        dto.setId(order.getId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setEmail(order.getEmail());
        dto.setCity(order.getCity());
        dto.setAddress(order.getAddress());
        dto.setPostIndex(order.getPostIndex());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setDate(order.getDate());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemMailDto> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemMailDto i = new OrderItemMailDto();
                    i.setPerfumer(item.getPerfume().getPerfumer());
                    i.setPerfumeTitle(item.getPerfume().getPerfumeTitle());
                    i.setType(item.getPerfume().getType());
                    i.setVolume(item.getPerfume().getVolume());
                    i.setQuantity(item.getQuantity());
                    i.setPrice(item.getPerfume().getPrice());
                    i.setImagePath(item.getPerfume().getFilename());
                    return i;
                })
                        .collect(Collectors.toList());
        dto.setItems(items);
        return dto;
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

        OrderMailDto orderMailDto = mapToMailDto(order);

        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(order.getEmail());
        mailRequest.setSubject("Order #" + order.getId());
        mailRequest.setTemplate("order-template");
        mailRequest.setAttributes(Map.of("order", orderMailDto));

        String token = jwtProvider.createMailToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MailRequest> entity = new HttpEntity<>(mailRequest, headers);

        // Send POST request
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(notificationApiUrl, entity, Void.class);

        if (responseEntity.getStatusCode() != HttpStatus.ACCEPTED){
            throw new ApiRequestException(MAIL_NOT_SENT, HttpStatus.NOT_FOUND);
        }

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

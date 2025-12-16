package org.ecommerce.productapi.dto.order;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderMailDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String address;
    private Integer postIndex;
    private String phoneNumber;
    private LocalDate date;
    private Double totalPrice;
    private List<OrderItemMailDto> items;
}

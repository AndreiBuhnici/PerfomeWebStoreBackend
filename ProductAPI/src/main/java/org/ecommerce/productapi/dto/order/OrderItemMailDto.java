package org.ecommerce.productapi.dto.order;

import lombok.Data;

@Data
public class OrderItemMailDto {
    private String perfumer;
    private String perfumeTitle;
    private String type;
    private String volume;
    private Long quantity;
    private Integer price;
    private String imagePath;
}

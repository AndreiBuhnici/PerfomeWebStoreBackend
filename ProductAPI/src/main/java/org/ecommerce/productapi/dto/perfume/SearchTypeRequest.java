package org.ecommerce.productapi.dto.perfume;

import org.ecommerce.productapi.enums.SearchPerfume;
import lombok.Data;

@Data
public class SearchTypeRequest {
    private SearchPerfume searchType;
    private String text;
}

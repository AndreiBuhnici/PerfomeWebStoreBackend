package org.ecommerce.productapi.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface PerfumeProjection {
    Long getId();
    String getPerfumeTitle();
    String getPerfumer();
    Integer getPrice();
    String getFilename();
    Double getPerfumeRating();

    void setPerfumer(String perfumer);
    void setPerfumeGender(String perfumeGender);
    void setPrice(Integer price);
}

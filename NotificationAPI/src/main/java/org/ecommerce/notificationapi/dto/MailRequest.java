package org.ecommerce.notificationapi.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Map;

import static org.ecommerce.notificationapi.constants.ErrorMessage.*;

@Data
public class MailRequest {
    @Email(message = INCORRECT_EMAIL)
    @NotBlank(message = EMAIL_CANNOT_BE_EMPTY)
    private String to;

    @NotBlank(message = FILL_IN_THE_INPUT_FIELD)
    private String subject;

    @NotBlank(message = FILL_IN_THE_INPUT_FIELD)
    private String template;

    private Map<String, Object> attributes;
}

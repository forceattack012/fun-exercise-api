package com.javabootcamp.fintechbank.accounts;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountRequest(
        @NotNull(message = "should not be null")
        @NotEmpty(message = "should not be empty")
        @NotBlank(message = "should not be blank")
        @Pattern(regexp = "[A-Z][a-zA-Z]*", message = "should allow only upper character")
        String type,
        @NotNull(message = "should not be null")
        @NotEmpty(message = "should not be empty")
        @NotBlank(message = "should not be blank")
        String name,
        @Positive
        Double balance) {
}

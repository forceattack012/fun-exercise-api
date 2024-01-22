package com.javabootcamp.fintechbank.accounts;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Entity(name = "accounts")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // If we found an error try to replace this with GenerationType.IDENTITY
    private Integer no;
    @NotEmpty
    private String type;
    private String name;
    @NotNull
    private Double balance;
}

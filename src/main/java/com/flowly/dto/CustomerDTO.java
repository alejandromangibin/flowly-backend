package com.flowly.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private UUID customerId;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String type; // "lead", "prospect", "customer"
}

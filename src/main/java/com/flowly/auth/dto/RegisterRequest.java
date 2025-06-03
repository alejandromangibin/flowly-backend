// --- DTOs ---
package com.flowly.auth.dto;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private UUID tenantId;
}



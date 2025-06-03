package com.flowly;
// --- TestController.java ---

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/test")
public class TestController {

    // @GetMapping("/secure")
    // public ResponseEntity<String> securedEndpoint(Authentication auth) {
    //     return ResponseEntity.ok("You are authenticated as: " + auth.getName());
    // }

    @GetMapping("/secure")
    @PreAuthorize("hasAuthority('ROLE_USER')") // or @PreAuthorize("isAuthenticated()")
    public String secure() {
        return "You accessed a secured endpoint!";
    }
}

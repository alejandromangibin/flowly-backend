package com.flowly.controller;

import com.flowly.dto.CreateCustomerRequest;
import com.flowly.dto.UpdateCustomerRequest;
import com.flowly.dto.CustomerDTO;
import com.flowly.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "CRUD operations for CRM customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
        summary = "Get all customers",
        description = "Returns a list of all customers in the current tenant schema"
    )
    @ApiResponse(responseCode = "200", description = "List of customers retrieved",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CustomerDTO.class)))
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        System.out.println("Fetching all customers");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(
        summary = "Get customer by ID",
        description = "Returns a single customer based on UUID"
    )
    @ApiResponse(responseCode = "200", description = "Customer retrieved",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CustomerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Operation(
        summary = "Create a new customer",
        description = "Adds a new customer to the database after validating for duplicates"
    )
    @ApiResponse(responseCode = "200", description = "Customer created",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CustomerDTO.class)))
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        try {
            return ResponseEntity.ok(customerService.createCustomer(request));
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, log to a logger as well
            throw e; // rethrow to preserve default error handling
        }
    }

    @Operation(
        summary = "Update an existing customer",
        description = "Updates customer fields by ID"
    )
    @ApiResponse(responseCode = "200", description = "Customer updated",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CustomerDTO.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
                System.out.println("Updating customer with ID: " + id);
                System.out.println("Update request: " + request);
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @Operation(
        summary = "Delete a customer",
        description = "Deletes a customer record by ID"
    )
    @ApiResponse(responseCode = "204", description = "Customer deleted")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

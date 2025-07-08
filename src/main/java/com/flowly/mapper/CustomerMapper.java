/*
| Method                        | Purpose                                             |
| ----------------------------- | --------------------------------------------------- |
| `toDTO()`                     | Convert `Customer` entity → `CustomerDTO`           |
| `toEntity()`                  | Convert `CreateCustomerRequest` → `Customer`        |
| `updateCustomerFromRequest()` | Update existing entity from `UpdateCustomerRequest` |

 */
package com.flowly.mapper;


import com.flowly.dto.CustomerDTO;
import com.flowly.dto.UpdateCustomerRequest;
import com.flowly.dto.CreateCustomerRequest;
import com.flowly.shared.model.Customer;
import com.flowly.shared.model.Customer.CustomerType;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("CustomerMapper~ Converting Customer to DTO: " + customer);
        return CustomerDTO.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .company(customer.getCompany())
                .type(customer.getType().name().toString().toLowerCase()) // e.g., "lead"
                .build();
    }

    public static Customer toEntity(CreateCustomerRequest request) {
     
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .company(request.getCompany())
                .type(CustomerType.valueOf(request.getType().toUpperCase())) // Convert string to enum
                
                .build();
        System.out.println("Creating customer from request: " + request);
        System.out.println("Customer entity created: " + customer);
        System.out.println("Customer type set to: " + customer.getType());
        return customer;

        
    }

    public static void updateCustomerFromRequest(UpdateCustomerRequest request, Customer customer) {
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setCompany(request.getCompany());
        customer.setType(CustomerType.valueOf(request.getType().toUpperCase()));

        

    }
}

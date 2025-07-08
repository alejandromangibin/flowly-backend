package com.flowly.service;

import com.flowly.dto.CreateCustomerRequest;
import com.flowly.dto.UpdateCustomerRequest;
import com.flowly.dto.CustomerDTO;
import com.flowly.mapper.CustomerMapper;
import com.flowly.shared.model.Customer;
import com.flowly.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        return CustomerMapper.toDTO(customer);
    }

    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists: " + request.getEmail());
        }
        Customer customer = CustomerMapper.toEntity(request);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Creating customer from request: " + request);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer entity created: " + customer);
        customer = customerRepository.save(customer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved: " + customer);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer type set to: " + customer.getType());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with ID: " + customer.getCustomerId());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with email: " + customer.getEmail());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with phone: " + customer   .getPhone());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with company: " + customer.getCompany());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with type: " + customer.getType());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with name: " + customer.getName());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CustomerService.java ~ Customer saved with customerId: " + customer.getCustomerId());
        
        return CustomerMapper.toDTO(customer);
    }

    public CustomerDTO updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        CustomerMapper.updateCustomerFromRequest(request, customer);
        return CustomerMapper.toDTO(customerRepository.save(customer));
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}

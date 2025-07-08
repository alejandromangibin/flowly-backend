package com.flowly.ui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowly.config.ApiConfig;
// import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Route("tenant-register")
public class TenantRegisterView extends VerticalLayout {

    private final ApiConfig apiConfig;
    private final TextField businessNameField = new TextField("Business Name");
    private final EmailField ownerEmailField = new EmailField("Owner Email");
    private final TextField subdomainField = new TextField("Subdomain");
    private final TextField birTinField = new TextField("BIR TIN");
    private final ComboBox<String> businessTypeField = new ComboBox<>("Business Type");
    private final TextField industryField = new TextField("Industry");
    private final PasswordField passwordField = new PasswordField("Admin Password");
    private final ComboBox<String> subscriptionPlanField = new ComboBox<>("Subscription Plan");

    // private final Button checkButton = new Button("Check Subdomain");
    private final Button registerButton = new Button("Register Tenant");

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TenantRegisterView(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        setSizeFull();
        // setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        VerticalLayout card = new VerticalLayout();
        card.setWidth("450px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.1)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");

        H1 title = new H1("Register a New Tenant");
        Paragraph subtitle = new Paragraph("Create a business account on Flowly CRM");

        // Dropdown setup
        businessTypeField.setItems("Corporation", "Partnership", "Sole Proprietorship");
        businessTypeField.setPlaceholder("Select a type");

        subscriptionPlanField.setItems("Free", "Pro", "Enterprise");
        subscriptionPlanField.setPlaceholder("Choose plan");

        
        subdomainField.addBlurListener(e -> checkSubdomainAvailability());
        registerButton.addClickListener(e -> registerTenant());

        // checkButton.getStyle().set("margin-top", "0.5rem");
        registerButton.getStyle().set("background", "#22c55e").set("color", "white").set("width", "100%");

        FormLayout form = new FormLayout(
                businessNameField,
                ownerEmailField,
                subdomainField,
                // checkButton,
                birTinField,
                businessTypeField,
                industryField,
                passwordField,
                subscriptionPlanField,
                registerButton
        );

        form.setColspan(registerButton, 2);
        

        card.add(title, subtitle, form);
        add(card);
    }

    @SuppressWarnings("rawtypes")
    private void checkSubdomainAvailability() {
        String subdomain = subdomainField.getValue().trim();

        if (subdomain.isEmpty()) {
            showError("Subdomain is required");
            return;
        }

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    apiConfig.getApiBaseUrl() + "/tenants/check-subdomain?subdomain=" + subdomain,
                    Map.class
            );
            boolean available = (Boolean) response.getBody().get("available");
            if (available) {
                showSuccess("Subdomain is available ✅");
            } else {
                showError("Subdomain is already taken ❌");
            }
        } catch (Exception e) {
            showError("Error checking subdomain: " + e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private void registerTenant() {
        String businessName = businessNameField.getValue().trim();
        String ownerEmail = ownerEmailField.getValue().trim();
        String subdomain = subdomainField.getValue().trim();
        String birTin = birTinField.getValue().trim();
        String businessType = businessTypeField.getValue();
        String industry = industryField.getValue().trim();
        String password = passwordField.getValue().trim();
        String subscriptionPlan = subscriptionPlanField.getValue();

        if (businessName.isEmpty() || ownerEmail.isEmpty() || subdomain.isEmpty() ||
            birTin.isEmpty() || businessType == null || industry.isEmpty() ||
            password.isEmpty() || subscriptionPlan == null) {
            showError("Please fill in all required fields.");
            return;
        }

        Map<String, Object> tenantRequest = new LinkedHashMap<>();
        tenantRequest.put("businessName", businessName);
        tenantRequest.put("ownerEmail", ownerEmail);
        tenantRequest.put("subdomain", subdomain);
        tenantRequest.put("birTin", birTin);
        tenantRequest.put("businessType", businessType.toLowerCase().replace(" ", "_"));
        tenantRequest.put("industry", industry);
        tenantRequest.put("password", password);
        tenantRequest.put("subscriptionPlan", subscriptionPlan);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(tenantRequest), headers);

            ResponseEntity<Map> tenantResponse = restTemplate.postForEntity(
                    apiConfig.getApiBaseUrl() + "/tenants/register",
                    entity,
                    Map.class
            );

            if (tenantResponse.getStatusCode().is2xxSuccessful()) {
                showSuccess("Tenant registered! 🎉");

                // Step 2: Register admin user
                registerAdminUser(ownerEmail, password, subdomain);

            } else {
                showError("Tenant registration failed: " + tenantResponse.getBody());
            }
        } catch (Exception e) {
            showError("Error during tenant registration: " + e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private void registerAdminUser(String email, String password, String tenantId) {
        Map<String, Object> userRequest = Map.of(
                "username", email.split("@")[0], // e.g., 'admin' from 'admin@flowly.com'
                "email", email,
                "password", password,
                "role", "ADMIN"
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Tenant-ID", tenantId);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(userRequest), headers);

            ResponseEntity<Map> userResponse = restTemplate.postForEntity(
                    apiConfig.getApiBaseUrl() + "/auth/register",
                    entity,
                    Map.class
            );

            if (userResponse.getStatusCode().is2xxSuccessful()) {
                showSuccess("Admin user registered! 👑 You can now log in.");
            } else {
                showError("Admin registration failed: " + userResponse.getBody());
            }
        } catch (Exception e) {
            showError("Error registering admin user: " + e.getMessage());
        }
    }


    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

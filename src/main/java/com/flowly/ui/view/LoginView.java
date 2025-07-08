package com.flowly.ui.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Route("login")
public class LoginView extends HorizontalLayout {

    @Value("${flowly.api.base-url}")
    private String baseUrl;

    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final Button loginButton = new Button("Login");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public LoginView() {
        setSizeFull();

        // ==== Left Panel (Login Form) ====
        // VerticalLayout formLayout = new VerticalLayout();
        // formLayout.setSizeFull();
        // formLayout.setAlignItems(Alignment.CENTER);
        // formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        // formLayout.setWidth("50%");
        // formLayout.getStyle().set("padding", "40px");
        // formLayout.getStyle()
        //     .set("background", "linear-gradient(135deg, #7F00FF 0%, #E100FF 100%)")
        //     .set("color", "white")
        //     .set("justify-content", "center")
        //     .set("align-items", "center")
        //     .set("display", "flex");

        // H1 title = new H1("Login to Flowly");
        // title.getStyle()
        //     .set("font-size", "2em")
        //     .set("font-weight", "bold")
        //     .set("letter-spacing", "1px");
            // .set("text-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)");

        emailField.setWidth("300px");
        emailField.setPlaceholder("Enter your email");
        emailField.setClearButtonVisible(true);
        emailField.setRequiredIndicatorVisible(true);
        

        passwordField.setWidth("300px");
        passwordField.setPlaceholder("Enter your password");
        passwordField.setRevealButtonVisible(true);
        passwordField.setMinLength(8);
        passwordField.setClearButtonVisible(true);
        passwordField.setRequiredIndicatorVisible(true);

        emailField.getStyle().set("--vaadin-input-field-default-text-color", "white");
        emailField.getStyle().set("--vaadin-input-field-label-color", "white");
        emailField.getStyle().set("--vaadin-input-field-placeholder-color", "#eeeeee");
        emailField.getStyle().set("--vaadin-input-field-value-color", "white");
        emailField.getStyle().set("--vaadin-input-field-focused-label-color","rgb(255, 166, 0)");

        passwordField.getStyle().set("--vaadin-input-field-default-text-color", "white");
        passwordField.getStyle().set("--vaadin-input-field-label-color", "white");
        passwordField.getStyle().set("--vaadin-input-field-placeholder-color", "#eeeeee");
        passwordField.getStyle().set("--vaadin-input-field-value-color", "white");
        passwordField.getStyle().set("--vaadin-input-field-focused-label-color","rgb(255, 166, 0)");
        
        loginButton.getStyle()
            .set("background-color", "rgb(255, 166, 0)")
            .set("color", "white")
            .set("border", "solid 1px rgb(255, 255, 255)")
            .set("border-radius", "4px")
            .set("padding", "10px 20px")
            .set("font-size", "1em")
            .set("cursor", "pointer");
        loginButton.getStyle().set("width", "300px");
        loginButton.addClickListener(e -> {
            UI.getCurrent().getPage().fetchCurrentURL(url -> {
                String subdomain = extractSubdomainFromHost(url.getHost());
                login(subdomain);
            });
        });

        // formLayout.add(title, emailField, passwordField, loginButton);

        // ==== Right Panel (Decorative) ====
        VerticalLayout designPanel = new VerticalLayout();
        designPanel.setSizeFull();
        // designPanel.setWidth("60%");
        designPanel.getStyle()
            .set("background", "linear-gradient(135deg, #7F00FF 0%, #E100FF 100%)")
            .set("color", "white")
            .set("justify-content", "center")
            .set("align-items", "center")
            .set("display", "flex");

        Span welcome = new Span("Welcome to Flowly ✨");
        welcome.getStyle()
            .set("font-size", "2em")
            .set("font-weight", "bold");

        Image image = new Image("/images/flowly-logo1.png", "Flowly illustration");
        image.setWidth("300px");

        designPanel.add(welcome, image);
        designPanel.add( emailField, passwordField, loginButton);

        add(designPanel);
    }

    private void login(String tenantId) {
        String email = emailField.getValue().trim();
        String password = passwordField.getValue().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password");
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", tenantId);

        try {
            HttpEntity<String> loginRequest = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
            ResponseEntity<JsonNode> loginResponse = restTemplate.postForEntity(baseUrl + "/auth/login", loginRequest, JsonNode.class);

            if (loginResponse.getStatusCode().is2xxSuccessful()) {
                String token = loginResponse.getBody().get("token").asText();
                fetchUserAndRedirect(token);
            } else {
                showError("Invalid credentials");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void fetchUserAndRedirect(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(baseUrl + "/auth/me", HttpMethod.GET, entity, JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String role = response.getBody().get("role").asText();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    UI.getCurrent().navigate("admin-dashboard");
                } else if ("USER".equalsIgnoreCase(role)) {
                    UI.getCurrent().navigate("user-dashboard");
                } else {
                    showError("Unknown role: " + role);
                }
            } else {
                showError("Failed to retrieve user info");
            }
        } catch (Exception e) {
            showError("Token validation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private String extractSubdomainFromHost(String host) {
        String[] parts = host.split("\\.");
        return parts.length >= 2 ? parts[0] : "public";
    }

    private String getInlineSVG() {
        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 300 200'><defs><linearGradient id='a' x1='0' y1='0' x2='1' y2='1'><stop offset='0%' stop-color='#fff'/><stop offset='100%' stop-color='#dcdcdc'/></linearGradient></defs><path d='M0,100 C75,200 225,0 300,100 L300,200 L0,200 Z' fill='url(#a)'/></svg>";
    }
}

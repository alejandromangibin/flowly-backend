package com.flowly.ui.view;

import com.flowly.auth.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final TextField usernameField = new TextField("Username");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final TextField tenantField = new TextField("Tenant (subdomain)");
    private final ComboBox<String> roleField = new ComboBox<>("Role");

    private final Button registerButton = new Button("Register");

    public RegisterView() {

        tenantField.setWidthFull();
        usernameField.setWidthFull();
        emailField.setWidthFull();
        passwordField.setWidthFull();
        roleField.setWidthFull();
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(getDefaultHorizontalComponentAlignment());
        
        card.getStyle().set("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.1)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");

        H1 title = new H1("Flowly Registration");
        Paragraph subtitle = new Paragraph("Create your organization and admin user");

        roleField.setItems("ADMIN", "USER");
        roleField.setPlaceholder("Select Role");

        registerButton.addClickListener(e -> handleRegister());
        registerButton.getStyle().set("width", "100%");
        registerButton.getStyle().set("background", "#2563EB");
        registerButton.getStyle().set("color", "white");

        card.add(title, subtitle, tenantField, usernameField, emailField, passwordField, roleField, registerButton);
        add(card);
    }

    private void handleRegister() {
        String tenantId = tenantField.getValue().trim();
        String email = emailField.getValue().trim();
        String username = usernameField.getValue().trim();
        String password = passwordField.getValue().trim();
        String role = roleField.getValue();

        if (tenantId.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
            showError("Please fill in all fields");
            return;
        }

        try {
            RegisterRequest request = new RegisterRequest(username, email, password, role);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Tenant-ID", tenantId);

            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/api/auth/register", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                showSuccess("Registration successful! 🎉");
            } else {
                showError("Registration failed: " + response.getBody());
            }

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

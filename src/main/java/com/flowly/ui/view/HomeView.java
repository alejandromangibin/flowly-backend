package com.flowly.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H1("Welcome to Flowly CRM"));
        add(new Button("Say Hello", e -> Notification.show("Hello from Vaadin!")));
    }
}

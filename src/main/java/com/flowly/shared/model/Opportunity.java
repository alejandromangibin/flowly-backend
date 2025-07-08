package com.flowly.shared.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opportunity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    @GeneratedValue
    @Column(name = "opportunity_id", updatable = false, nullable = false)
    private UUID opportunityId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    @Column(name = "expected_value", precision = 12, scale = 2, nullable = false)
    private BigDecimal expectedValue;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quote> quotes;

    public enum Stage {
        PROPOSAL,
        NEGOTIATION,
        CLOSED
    }
}

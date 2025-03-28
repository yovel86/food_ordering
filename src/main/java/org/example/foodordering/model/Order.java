package org.example.foodordering.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String requestId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @ElementCollection
    private List<Long> itemIds;

    private boolean isPaymentDone = false;

    private String trackingId;

}

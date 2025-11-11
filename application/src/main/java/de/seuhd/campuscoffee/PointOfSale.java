package com.campuscoffee.pos;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class PointOfSale {
    @Id @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private Long osmNodeId;

    private String name;
    private String category;
    private double latitude;
    private double longitude;
    private String street;
    private String houseNumber;
    private String postcode;
    private String city;
    private String openingHours;
    private String phone;
    private String website;
    private Instant updatedAt;

    @PrePersist @PreUpdate
    void touch() { this.updatedAt = Instant.now(); }

    // getters/setters …
}

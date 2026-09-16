package com.thartheeb.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_capability_requirements")
public class CapabilityRequirement {
    @Id private UUID id;
    @Column(nullable = false, length = 80) private String serviceCode;
    @Column(nullable = false) private int serviceVersion;
    @Column(nullable = false, length = 100) private String capabilityCode;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private Instant updatedAt;
    @Column(nullable = false, length = 120) private String updatedBy;
    @Version private long rowVersion;

    protected CapabilityRequirement() {}

    public CapabilityRequirement(String serviceCode, int serviceVersion, String capabilityCode,
                                 String updatedBy) {
        this.id = UUID.randomUUID();
        this.serviceCode = serviceCode;
        this.serviceVersion = serviceVersion;
        this.capabilityCode = capabilityCode;
        this.active = true;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    public void deactivate(String actor) {
        active = false;
        updatedAt = Instant.now();
        updatedBy = actor;
    }
    public UUID getId() { return id; }
    public String getServiceCode() { return serviceCode; }
    public int getServiceVersion() { return serviceVersion; }
    public String getCapabilityCode() { return capabilityCode; }
    public boolean isActive() { return active; }
    public Instant getUpdatedAt() { return updatedAt; }
}

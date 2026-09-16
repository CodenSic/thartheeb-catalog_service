package com.thartheeb.catalog.infrastructure.persistence;

import com.thartheeb.catalog.domain.CapabilityRequirement;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapabilityRequirementRepository extends JpaRepository<CapabilityRequirement, UUID> {
    List<CapabilityRequirement> findByServiceCodeAndServiceVersionAndActiveTrueOrderByCapabilityCodeAsc(
        String serviceCode, int serviceVersion);
}

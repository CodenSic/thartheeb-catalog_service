package com.thartheeb.catalog.application;

import static com.thartheeb.catalog.api.CapabilityContracts.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CapabilityRequirementServiceTests {
    @Autowired CapabilityRequirementService service;

    @Test
    void replacementNormalizesDeduplicatesAndChangesTheActiveSet() {
        String serviceCode = "service-" + UUID.randomUUID();
        var first = service.replace(serviceCode, 1,
            new ReplaceRequirementsRequest(List.of("hvac_cert", "HVAC_CERT")), "admin-1");
        assertThat(first).hasSize(1);

        service.replace(serviceCode, 1,
            new ReplaceRequirementsRequest(List.of("electrical-cert")), "admin-1");

        assertThat(service.get(serviceCode, 1).requiredCapabilities())
            .containsExactly("ELECTRICAL-CERT");
    }
}

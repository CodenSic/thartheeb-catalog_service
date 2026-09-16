package com.thartheeb.catalog.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CapabilityContracts {
    private CapabilityContracts() {}
    public record ReplaceRequirementsRequest(@NotEmpty List<@NotBlank String> capabilityCodes) {}
    public record CapabilityRequirementView(UUID id, String serviceCode, int serviceVersion,
                                            String capabilityCode, Instant updatedAt) {}
    public record CapabilitySet(String serviceCode, int serviceVersion,
                                List<String> requiredCapabilities) {}
}

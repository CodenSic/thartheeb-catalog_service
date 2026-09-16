package com.thartheeb.catalog.application;

import static com.thartheeb.catalog.api.CapabilityContracts.*;

import com.thartheeb.catalog.domain.CapabilityRequirement;
import com.thartheeb.catalog.infrastructure.persistence.CapabilityRequirementRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CapabilityRequirementService {
    private final CapabilityRequirementRepository requirements;

    public CapabilityRequirementService(CapabilityRequirementRepository requirements) {
        this.requirements = requirements;
    }

    @Transactional(readOnly = true)
    public CapabilitySet get(String serviceCode, int serviceVersion) {
        String normalizedService = normalize(serviceCode);
        List<String> capabilities = requirements
            .findByServiceCodeAndServiceVersionAndActiveTrueOrderByCapabilityCodeAsc(
                normalizedService, serviceVersion)
            .stream().map(CapabilityRequirement::getCapabilityCode).toList();
        return new CapabilitySet(normalizedService, serviceVersion, capabilities);
    }

    @Transactional
    public List<CapabilityRequirementView> replace(String serviceCode, int serviceVersion,
                                                   ReplaceRequirementsRequest request,
                                                   String actor) {
        String normalizedService = normalize(serviceCode);
        var current = requirements
            .findByServiceCodeAndServiceVersionAndActiveTrueOrderByCapabilityCodeAsc(
                normalizedService, serviceVersion);
        current.forEach(value -> value.deactivate(actor));
        var unique = new LinkedHashSet<String>();
        request.capabilityCodes().stream().map(CapabilityRequirementService::normalize)
            .forEach(unique::add);
        return requirements.saveAll(unique.stream()
                .map(capability -> new CapabilityRequirement(normalizedService, serviceVersion,
                    capability, actor)).toList())
            .stream().map(this::view).toList();
    }

    private CapabilityRequirementView view(CapabilityRequirement value) {
        return new CapabilityRequirementView(value.getId(), value.getServiceCode(),
            value.getServiceVersion(), value.getCapabilityCode(), value.getUpdatedAt());
    }

    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9_-]", "_");
    }
}

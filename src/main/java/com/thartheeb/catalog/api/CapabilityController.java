package com.thartheeb.catalog.api;

import static com.thartheeb.catalog.api.CapabilityContracts.*;

import com.thartheeb.catalog.application.CapabilityRequirementService;
import com.thartheeb.catalog.common.ApiError;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Service capability policy", description = "Required Technician capabilities by service version")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "401", description = "Bearer token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "403", description = "Admin role or internal scope required", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
})
public class CapabilityController {
    private final CapabilityRequirementService service;

    public CapabilityController(CapabilityRequirementService service) { this.service = service; }

    @GetMapping("/internal/v1/services/{serviceCode}/versions/{version}/capability-requirements")
    @Operation(summary = "Get capability requirements", tags = "Internal Catalog APIs",
        description = "Returns the active normalized capability set. An unconfigured version returns an empty set so Operations can fail closed.")
    public CapabilitySet internal(@PathVariable String serviceCode,
                                  @PathVariable @Positive int version) {
        return service.get(serviceCode, version);
    }

    @PutMapping("/v1/admin/services/{serviceCode}/versions/{version}/capability-requirements")
    @Operation(summary = "Replace capability requirements",
        description = "Deactivates the current set and writes a normalized, de-duplicated authoritative set for the exact service version.")
    public List<CapabilityRequirementView> replace(@PathVariable String serviceCode,
                                                    @PathVariable @Positive int version,
                                                    @Valid @RequestBody ReplaceRequirementsRequest request,
                                                    Authentication auth) {
        return service.replace(serviceCode, version, request, auth.getName());
    }
}

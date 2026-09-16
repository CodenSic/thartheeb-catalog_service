package com.thartheeb.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class OpenApiCoverageTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = webAppContextSetup(context).build();
    }

    @Test
    void separatesPublicAndInternalCatalogApis() throws Exception {
        String publicSpec = getSpec("/v3/api-docs/public");
        assertThat(publicSpec)
                .contains("/v1/admin/services/{serviceCode}/versions/{version}/capability-requirements")
                .contains("#/components/schemas/ApiError")
                .contains("\"status\"")
                .contains("\"error\"")
                .contains("\"message\"")
                .doesNotContain("/internal/v1/services/{serviceCode}/versions/{version}/capability-requirements");
        String internalSpec = getSpec("/v3/api-docs/internal");
        assertThat(internalSpec)
                .contains("/internal/v1/services/{serviceCode}/versions/{version}/capability-requirements")
                .contains("#/components/schemas/ApiError")
                .doesNotContain("/v1/admin/services/{serviceCode}/versions/{version}/capability-requirements");
    }

    private String getSpec(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
    }
}

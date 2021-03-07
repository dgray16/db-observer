package com.db_observer.app.documentation;

import com.db_observer.app.base.AbstractTest;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/** Tests for {@link com.db_observer.app.browsing.controller.BrowsingController} */
class BrowsingControllerTest extends AbstractTest {

    /** {@link com.db_observer.app.browsing.controller.BrowsingController#getSchemas(Long)} */
    @Test
    @Transactional
    void testGetSchemas() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/browsing/schemas")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString());

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "browsing/get-schemas",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("connectionConfigId").description("Id of Connection Config")
                    ),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("[].schemaName").description("Name of the Schema"),
                            PayloadDocumentation.fieldWithPath("[].schemaOwner").description("Owner of the Schema")
                    )
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(4)))
                    .andDo(document);
        }
    }

    /** {@link com.db_observer.app.browsing.controller.BrowsingController#getTables(String, Long)} */
    @Test
    void testGetTables() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get("/browsing/schemas/{schema}/tables", "public")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString());

            final ParameterDescriptor schemaParameter = RequestDocumentation
                    .parameterWithName("schema")
                    .description("Name of the Schema");

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "browsing/get-tables",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("connectionConfigId").description("Id of Connection Config")
                    ),
                    RequestDocumentation.pathParameters(schemaParameter),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("[].tableName").description("Name of the Table"),
                            PayloadDocumentation.fieldWithPath("[].tableOwner").description("Owner of the Table")
                    )
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(NumberUtils.INTEGER_ONE)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].tableName").value("customers"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].tableOwner").value("spring"))
                    .andDo(document);
        }
    }

    /** {@link com.db_observer.app.browsing.controller.BrowsingController#getColumns(String, String, Long)} */
    @Test
    void testGetColumns() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get("/browsing/schemas/{schema}/tables/{table}", "public", "customers")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString());

            final ParameterDescriptor schemaParameter = RequestDocumentation
                    .parameterWithName("schema")
                    .description("Name of the Schema");

            final ParameterDescriptor tableParameter = RequestDocumentation
                    .parameterWithName("table")
                    .description("Name of the Table");

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "browsing/get-columns",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("connectionConfigId").description("Id of Connection Config")
                    ),
                    RequestDocumentation.pathParameters(schemaParameter, tableParameter),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("[].columnName").description("Name of the Column"),
                            PayloadDocumentation.fieldWithPath("[].columnDefault").description("Columns default value"),
                            PayloadDocumentation.fieldWithPath("[].isNullable").description("Is Column nullable?"),
                            PayloadDocumentation.fieldWithPath("[].dataType").description("Data type of the Column"),
                            PayloadDocumentation.fieldWithPath("[].characterMaximumLength").description("Maximum length of column if character data type").optional()
                    )
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(NumberUtils.INTEGER_ONE)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].columnName").value("first_name"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].columnDefault").value("'random name'::character varying"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].isNullable").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].dataType").value("character varying"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].characterMaximumLength").value(255))
                    .andDo(document);
        }
    }

    /** {@link com.db_observer.app.browsing.controller.BrowsingController#getTablePreview(String, String, Long)} */
    @Test
    void testGetTablePreview() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get("/browsing/schemas/{schema}/tables/{table}/preview", "public", "customers")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString());

            final ParameterDescriptor schemaParameter = RequestDocumentation
                    .parameterWithName("schema")
                    .description("Name of the Schema");

            final ParameterDescriptor tableParameter = RequestDocumentation
                    .parameterWithName("table")
                    .description("Name of the Table");

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "browsing/get-table-preview",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("connectionConfigId").description("Id of Connection Config")
                    ),
                    RequestDocumentation.pathParameters(schemaParameter, tableParameter),
                    PayloadDocumentation.responseFields(PayloadDocumentation.subsectionWithPath("[].columnsWithValues").description("Map (key = column, value = list of values)"))
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(document);
        }
    }

}

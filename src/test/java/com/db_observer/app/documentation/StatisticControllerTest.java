package com.db_observer.app.documentation;

import com.db_observer.app.base.AbstractTest;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.statistic.model.request.GetStatisticRequest;
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

/** Tests for {@link com.db_observer.app.statistic.controller.StatisticController} */
class StatisticControllerTest extends AbstractTest {

    /** {@link com.db_observer.app.statistic.controller.StatisticController#getTablesStatistic(GetStatisticRequest)} */
    @Test
    @Transactional
    void testGetTableStatistic() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/statistic/tables")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString())
                    .queryParam("schema", "public");

            final ConstrainedFields constrainedFields = new ConstrainedFields(GetStatisticRequest.class);

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "statistic/get-tables-statistic",
                    RequestDocumentation.requestParameters(
                            constrainedFields.requestParamWithPath("connectionConfigId").description("ID of the Connection Config"),
                            constrainedFields.requestParamWithPath("schema").description("Schema to be searched in")
                    ),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.subsectionWithPath("records").description("Map (key = table, value = number of rows in table)"),
                            PayloadDocumentation.subsectionWithPath("attributes").description("Map (key = table, value = number of columns in table)")
                    )
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(document);
        }
    }

    /** {@link com.db_observer.app.statistic.controller.StatisticController#getColumnStatistic(String, GetStatisticRequest)} */
    @Test
    @Transactional
    void testGetColumnsStatistic() throws Exception {
        try (var container = createDatabase()) {

            final ConnectionConfig connectionConfig = createConnectionConfig(
                    container.getFirstMappedPort(),
                    container.getHost(),
                    container.getUsername(),
                    container.getPassword(),
                    container.getDatabaseName()
            );

            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get("/statistic/tables/{table}/columns", "customers")
                    .queryParam("connectionConfigId", connectionConfig.getId().toString())
                    .queryParam("schema", "public");

            final ConstrainedFields constrainedFields = new ConstrainedFields(GetStatisticRequest.class);

            final ParameterDescriptor tableParameter = RequestDocumentation
                    .parameterWithName("table")
                    .description("Name of the Table");

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "statistic/get-columns-statistic",
                    RequestDocumentation.requestParameters(
                            constrainedFields.requestParamWithPath("connectionConfigId").description("ID of the Connection Config"),
                            constrainedFields.requestParamWithPath("schema").description("Schema to be searched in")
                    ),
                    RequestDocumentation.pathParameters(tableParameter),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.subsectionWithPath("minValues").description("Map (key = column, value = min value)"),
                            PayloadDocumentation.subsectionWithPath("maxValues").description("Map (key = column, value = max value)"),
                            PayloadDocumentation.subsectionWithPath("avgValues").description("Map (key = column, value = average value)"),
                            PayloadDocumentation.subsectionWithPath("medianValues").description("Map (key = column, value = median value)")
                    )
            );

            mockMvc
                    .perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(document);
        }
    }

}

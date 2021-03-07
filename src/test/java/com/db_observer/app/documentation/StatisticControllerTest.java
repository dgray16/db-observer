package com.db_observer.app.documentation;

import com.db_observer.app.base.AbstractTest;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.statistic.model.request.GetTablesStatisticRequest;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/** Tests for {@link com.db_observer.app.statistic.controller.StatisticController} */
class StatisticControllerTest extends AbstractTest {

    /** {@link com.db_observer.app.statistic.controller.StatisticController#getTablesStatistic(GetTablesStatisticRequest)} */
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

            final ConstrainedFields constrainedFields = new ConstrainedFields(GetTablesStatisticRequest.class);

            final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                    "statistic/get-table-statistic",
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

}

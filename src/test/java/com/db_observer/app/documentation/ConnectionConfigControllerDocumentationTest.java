package com.db_observer.app.documentation;

import com.db_observer.app.base.AbstractTest;
import com.db_observer.app.connection_config.controller.ConnectionConfigController;
import com.db_observer.app.connection_config.model.request.CreateConnectionConfigRequest;
import com.db_observer.app.connection_config.model.request.UpdateConnectionConfigRequest;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
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
import org.testcontainers.shaded.com.google.common.collect.Iterables;

import java.util.List;

/** Tests for {@link com.db_observer.app.connection_config.controller.ConnectionConfigController} */
class ConnectionConfigControllerDocumentationTest extends AbstractTest {

    /** {@link ConnectionConfigController#getConnectionConfigurations()} */
    @Test
    @Transactional
    void testGetConnectionConfigurations() throws Exception {
        final ConnectionConfig connectionConfig = createConnectionConfig();

        final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                "connection-config/get-connection-configs",
                PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("[].username").description("User login name"),
                        PayloadDocumentation.fieldWithPath("[].connectionName").description("Name of database connection"),
                        PayloadDocumentation.fieldWithPath("[].databasePort").description("Database port"),
                        PayloadDocumentation.fieldWithPath("[].databaseName").description("Database name"),
                        PayloadDocumentation.fieldWithPath("[].databaseHostname").description("Host name of database")
                )
        );

        mockMvc
                .perform(MockMvcRequestBuilders.get("/connection-configurations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(NumberUtils.INTEGER_ONE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].username").value(connectionConfig.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].connectionName").value(connectionConfig.getConnectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].databasePort").value(connectionConfig.getDatabasePort()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].databaseHostname").value(connectionConfig.getDatabaseHostname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].databaseName").value(connectionConfig.getDatabaseName()))
                .andDo(document);
    }

    /** {@link ConnectionConfigController#createConnectionConfiguration(CreateConnectionConfigRequest)} */
    @Test
    @Transactional
    void testCreateConnectionConfig() throws Exception {
        final int connectionConfigsBefore = connectionConfigService.findAll().size();

        CreateConnectionConfigRequest request = prepareConnectionConfigRequest();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/connection-configurations")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        final ConstrainedFields constrainedFields = new ConstrainedFields(CreateConnectionConfigRequest.class);

        final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                "connection-config/create-connection-config",
                PayloadDocumentation.requestFields(
                        constrainedFields.payloadWithPath("username").description("User login name"),
                        constrainedFields.payloadWithPath("password").description("User login password"),
                        constrainedFields.payloadWithPath("connectionName").description("Name of database connection"),
                        constrainedFields.payloadWithPath("databasePort").description("Database port"),
                        constrainedFields.payloadWithPath("databaseName").description("Database name"),
                        constrainedFields.payloadWithPath("databaseHostname").description("Host name of database")
                )
        );

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(document);

        final List<ConnectionConfig> connectionConfigs = connectionConfigService.findAll();
        final int connectionConfigsAfter = connectionConfigs.size();
        final ConnectionConfig createdConnectionsConfig = Iterables.getFirst(connectionConfigs, null);

        Assertions.assertNotEquals(connectionConfigsBefore, connectionConfigsAfter);

        Assertions.assertEquals(request.getConnectionName(), createdConnectionsConfig.getConnectionName());
        Assertions.assertEquals(request.getUsername(), createdConnectionsConfig.getUsername());
        Assertions.assertEquals(request.getPassword(), createdConnectionsConfig.getPassword());
        Assertions.assertEquals(request.getDatabaseHostname(), createdConnectionsConfig.getDatabaseHostname());
        Assertions.assertEquals(request.getDatabaseName(), createdConnectionsConfig.getDatabaseName());
    }

    /** {@link ConnectionConfigController#updateConnectionConfiguration(Long, UpdateConnectionConfigRequest)} */
    @Test
    @Transactional
    void testUpdateConnectionConfig() throws Exception {
        final ConnectionConfig createdConnectionConfig = createConnectionConfig();
        UpdateConnectionConfigRequest request = prepareUpdateConnectionConfigRequest(createdConnectionConfig);

        final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .post("/connection-configurations/{connectionConfigId}", createdConnectionConfig.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        final ConstrainedFields constrainedFields = new ConstrainedFields(UpdateConnectionConfigRequest.class);

        final ParameterDescriptor idParameter = RequestDocumentation
                .parameterWithName("connectionConfigId")
                .description("Id of Connection Config");

        final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                "connection-config/update-connection-config",
                PayloadDocumentation.requestFields(
                        constrainedFields.payloadWithPath("username").description("User login name"),
                        constrainedFields.payloadWithPath("password").description("User login password"),
                        constrainedFields.payloadWithPath("connectionName").description("Name of database connection"),
                        constrainedFields.payloadWithPath("databasePort").description("Database port"),
                        constrainedFields.payloadWithPath("databaseName").description("Database name"),
                        constrainedFields.payloadWithPath("databaseHostname").description("Host name of database")
                ),
                RequestDocumentation.pathParameters(idParameter)
        );

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document);

        final ConnectionConfig updatedConnectionConfig = Iterables.getFirst(connectionConfigService.findAll(), null);

        Assertions.assertEquals(request.getConnectionName(), updatedConnectionConfig.getConnectionName());
        Assertions.assertEquals(request.getUsername(), updatedConnectionConfig.getUsername());
        Assertions.assertEquals(request.getPassword(), updatedConnectionConfig.getPassword());
        Assertions.assertEquals(request.getDatabasePort(), updatedConnectionConfig.getDatabasePort());
        Assertions.assertEquals(request.getDatabaseHostname(), updatedConnectionConfig.getDatabaseHostname());
        Assertions.assertEquals(request.getDatabaseName(), updatedConnectionConfig.getDatabaseName());
    }

    /** {@link ConnectionConfigController#deleteConnectionConfiguration(Long)} */
    @Test
    void testDeleteConnectionConfig() throws Exception {
        final ConnectionConfig createdConnectionConfig = createConnectionConfig();
        final int connectionsBefore = connectionConfigService.findAll().size();

        final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .delete("/connection-configurations/{connectionConfigId}", createdConnectionConfig.getId());

        final ParameterDescriptor idParameter = RequestDocumentation
                .parameterWithName("connectionConfigId")
                .description("Id of Connection Config");

        final RestDocumentationResultHandler document = MockMvcRestDocumentation.document(
                "connection-config/delete-connection-config", RequestDocumentation.pathParameters(idParameter)
        );

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(document);

        final int connectionsAfter = connectionConfigService.findAll().size();

        Assertions.assertNotEquals(connectionsBefore, connectionsAfter);
    }

    private UpdateConnectionConfigRequest prepareUpdateConnectionConfigRequest(ConnectionConfig entity) {
        final UpdateConnectionConfigRequest result = new UpdateConnectionConfigRequest();

        result.setConnectionName("New name");
        result.setDatabaseName(entity.getDatabaseName());
        result.setPassword(entity.getPassword());
        result.setUsername(entity.getUsername());
        result.setDatabasePort(entity.getDatabasePort());
        result.setDatabaseHostname(entity.getDatabaseHostname());

        return result;
    }

    private CreateConnectionConfigRequest prepareConnectionConfigRequest() {
        final CreateConnectionConfigRequest result = new CreateConnectionConfigRequest();

        result.setConnectionName("Spring DB");
        result.setUsername("spring");
        result.setPassword("secret");
        result.setDatabaseName("spring_db");
        result.setDatabaseHostname("localhost");
        result.setDatabasePort(5432);

        return result;
    }

    private ConnectionConfig createConnectionConfig() {
        return connectionConfigService.create(entity -> {
            entity.setUsername("spring");
            entity.setPassword("secret");
            entity.setConnectionName("Spring Database");
            entity.setDatabasePort(5432);
            entity.setDatabaseHostname("localhost");
            entity.setDatabaseName("spring_db");
        });
    }

}

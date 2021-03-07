package com.db_observer.app.base;

import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Setter(onMethod = @__({@Autowired}))
@FieldDefaults(level = AccessLevel.PROTECTED)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public abstract class AbstractTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    ConnectionConfigService connectionConfigService;

    protected PostgreSQLContainer<?> createDatabase() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.2-alpine")
                .withPassword("secret")
                .withUsername("spring")
                .withDatabaseName("spring_db")
                .withInitScript("browsing/init.sql");

        container.start();

        return container;
    }

    protected ConnectionConfig createConnectionConfig(Integer port, String host, String username, String password, String databaseName) {
        return connectionConfigService.create(entity -> {
            entity.setDatabasePort(port);
            entity.setDatabaseHostname(host);
            entity.setConnectionName("Spring DB");
            entity.setUsername(username);
            entity.setPassword(password);
            entity.setDatabaseName(databaseName);
        });
    }


    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    protected static class ConstrainedFields {

        ConstraintDescriptions constraintDescriptions;

        public ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        public FieldDescriptor payloadWithPath(String path) {
            return PayloadDocumentation.fieldWithPath(path).attributes(getAttributes(path));
        }

        public ParameterDescriptor requestParamWithPath(String path) {
            return RequestDocumentation.parameterWithName(path).attributes(getAttributes(path));
        }

        private Attributes.Attribute getAttributes(String path) {
            return Attributes
                    .key("constraints")
                    .value(String.join(". ", constraintDescriptions.descriptionsForProperty(path)));
        }

    }

}

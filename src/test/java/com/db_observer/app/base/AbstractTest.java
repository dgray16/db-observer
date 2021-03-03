package com.db_observer.app.base;

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
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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


    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    protected static class ConstrainedFields {

        ConstraintDescriptions constraintDescriptions;

        public ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        public FieldDescriptor withPath(String path) {
            final Attributes.Attribute constraints = Attributes
                    .key("constraints")
                    .value(String.join(". ", constraintDescriptions.descriptionsForProperty(path)));

            return PayloadDocumentation.fieldWithPath(path).attributes(constraints);
        }
    }

}

package com.db_observer.app.config;

import com.db_observer.app.domain.service.ConnectionConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitializer implements ApplicationListener<ApplicationReadyEvent> {

    ConnectionConfigService connectionConfigService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        createConnectionConfigurations();
    }

    private void createConnectionConfigurations() {
        connectionConfigService.create(entity -> {
            entity.setConnectionName("Local Customers Database");
            entity.setDatabaseHostname("localhost");
            entity.setDatabasePort(5432);
            entity.setDatabaseName("customers_db");
            entity.setUsername("customers");
            entity.setPassword("secret");
        });
    }

}

package com.db_observer.app.connection_config.service;

import com.db_observer.app.connection_config.model.dto.ConnectionConfigDto;
import com.db_observer.app.connection_config.model.request.CreateConnectionConfigRequest;
import com.db_observer.app.connection_config.model.request.UpdateConnectionConfigRequest;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConnectionConfigWebService {

    ConnectionConfigService connectionConfigService;

    @Transactional(readOnly = true)
    public List<ConnectionConfigDto> getConnectionConfigurations() {
        return connectionConfigService
                .findAll()
                .stream()
                .map(ConnectionConfigDto::of)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void createConnectionConfiguration(CreateConnectionConfigRequest request) {
        connectionConfigService.create(entity -> {
            entity.setUsername(request.getUsername());
            entity.setPassword(request.getPassword());
            entity.setDatabaseName(request.getDatabaseName());
            entity.setDatabasePort(request.getDatabasePort());
            entity.setDatabaseHostname(request.getDatabaseHostname());
            entity.setConnectionName(request.getConnectionName());
        });
    }

    @Transactional
    public void updateConnectionConfig(Long connectionConfigurationId, UpdateConnectionConfigRequest request) {
        final ConnectionConfig foundConnectionConfig = connectionConfigService.getOne(connectionConfigurationId);

        foundConnectionConfig.setConnectionName(request.getConnectionName());
        foundConnectionConfig.setUsername(request.getUsername());
        foundConnectionConfig.setPassword(request.getPassword());
        foundConnectionConfig.setDatabaseHostname(request.getDatabaseHostname());
        foundConnectionConfig.setDatabasePort(request.getDatabasePort());
        foundConnectionConfig.setDatabaseName(request.getDatabaseName());

        connectionConfigService.update(foundConnectionConfig);
    }

    @Transactional
    public void deleteConnectionConfiguration(Long connectionConfigurationId) {
        connectionConfigService.delete(connectionConfigurationId);
    }

}

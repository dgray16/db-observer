package com.db_observer.app.connection_config.model.dto;

import com.db_observer.app.domain.model.entity.ConnectionConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionConfigDto {

    String connectionName;
    String databaseHostname;
    Integer databasePort;
    String databaseName;
    String username;

    public static ConnectionConfigDto of(ConnectionConfig entity) {
        final ConnectionConfigDto result = new ConnectionConfigDto();

        result.setConnectionName(entity.getConnectionName());
        result.setDatabaseHostname(entity.getDatabaseHostname());
        result.setDatabasePort(entity.getDatabasePort());
        result.setDatabaseName(entity.getDatabaseName());
        result.setUsername(entity.getUsername());

        return result;
    }

}

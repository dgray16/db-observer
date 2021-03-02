package com.db_observer.app.connection_config.model;

import com.db_observer.app.domain.model.entity.ConnectionConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionConfigDto {

    String name;
    String hostname;
    Integer port;
    String databaseName;
    String username;

    public static ConnectionConfigDto of(ConnectionConfig entity) {
        final ConnectionConfigDto result = new ConnectionConfigDto();

        result.setName(entity.getConnectionName());
        result.setHostname(entity.getDatabaseHostname());
        result.setPort(entity.getDatabasePort());
        result.setDatabaseName(entity.getDatabaseName());
        result.setUsername(entity.getUsername());

        return result;
    }

}

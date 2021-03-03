package com.db_observer.app.connection_config.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseModifyingConnectionConfigRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    String connectionName;

    @NotBlank
    @Size(min = 5, max = 255)
    String databaseHostname;

    @NotNull
    @Min(value = 1000L)
    @Max(value = 9999L)
    Integer databasePort;

    @NotBlank
    @Size(min = 4, max = 100)
    String databaseName;

    @NotBlank
    @Size(min = 2, max = 80)
    String username;

    @NotBlank
    @Size(min = 4, max = 50)
    String password;

}

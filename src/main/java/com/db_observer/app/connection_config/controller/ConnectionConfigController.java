package com.db_observer.app.connection_config.controller;

import com.db_observer.app.connection_config.model.request.UpdateConnectionConfigRequest;
import com.db_observer.app.connection_config.model.dto.ConnectionConfigDto;
import com.db_observer.app.connection_config.model.request.CreateConnectionConfigRequest;
import com.db_observer.app.connection_config.service.ConnectionConfigWebService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConnectionConfigController {

    ConnectionConfigWebService connectionConfigWebService;

    @GetMapping(value = "connection-configurations")
    public ResponseEntity<List<ConnectionConfigDto>> getConnectionConfigurations() {
        return ResponseEntity.ok(connectionConfigWebService.getConnectionConfigurations());
    }

    @PostMapping(value = "connection-configurations")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createConnectionConfiguration(@Valid @RequestBody CreateConnectionConfigRequest request) {
        connectionConfigWebService.createConnectionConfiguration(request);
    }

    @PostMapping(value = "connection-configurations/{connectionConfigurationId}")
    public void updateConnectionConfiguration(@PathVariable Long connectionConfigurationId,
                                              @Valid @RequestBody UpdateConnectionConfigRequest request) {

        connectionConfigWebService.updateConnectionConfig(connectionConfigurationId, request);
    }

    @DeleteMapping(value = "connection-configurations/{connectionConfigurationId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteConnectionConfiguration(@PathVariable Long connectionConfigurationId) {
        connectionConfigWebService.deleteConnectionConfiguration(connectionConfigurationId);
    }

}

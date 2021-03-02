package com.db_observer.app.connection_config.controller;

import com.db_observer.app.connection_config.model.ConnectionConfigDto;
import com.db_observer.app.connection_config.service.ConnectionConfigWebService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

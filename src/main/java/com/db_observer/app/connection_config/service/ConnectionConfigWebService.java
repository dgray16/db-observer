package com.db_observer.app.connection_config.service;

import com.db_observer.app.connection_config.model.ConnectionConfigDto;
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

}

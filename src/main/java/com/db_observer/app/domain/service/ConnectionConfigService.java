package com.db_observer.app.domain.service;

import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.repository.jpa.ConnectionConfigRepository;
import com.db_observer.app.domain.service.base.DefaultCrudSupport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionConfigService extends DefaultCrudSupport<ConnectionConfig> {

    ConnectionConfigRepository jpaRepository;

    public ConnectionConfigService(ConnectionConfigRepository repository) {
        super(repository);
        this.jpaRepository = repository;
    }

}

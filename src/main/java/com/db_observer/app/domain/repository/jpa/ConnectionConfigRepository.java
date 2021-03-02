package com.db_observer.app.domain.repository.jpa;

import com.db_observer.app.domain.model.entity.ConnectionConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionConfigRepository extends JpaRepository<ConnectionConfig, Long> {
}

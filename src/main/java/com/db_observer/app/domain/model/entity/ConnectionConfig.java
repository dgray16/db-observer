package com.db_observer.app.domain.model.entity;

import com.db_observer.app.domain.model.entity.base.AbstractVersional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@ToString(callSuper = true, of = "")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionConfig extends AbstractVersional {

    /**
     * Custom name of the database instance.
     * Example: 'My Local Database'
     */
    @Column(nullable = false, length = 100)
    String connectionName;

    /**
     * Hostname of the database.
     * Example: localhost
     */
    @Column(nullable = false)
    String databaseHostname;

    /**
     * Port of the database.
     * Example: 5432
     */
    @Column(nullable = false)
    Integer databasePort;

    /**
     * Name of the database.
     * Example: db_observer
     */
    @Column(nullable = false, length = 100)
    String databaseName;

    /**
     * Username for the connection.
     * Example: observer
     */
    @Column(nullable = false, length = 80)
    String username;

    /**
     * Password for the connection.
     * Example: secret
     */
    @Column(nullable = false, length = 50)
    String password;

}

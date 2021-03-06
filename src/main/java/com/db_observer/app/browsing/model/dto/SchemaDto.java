package com.db_observer.app.browsing.model.dto;

import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
public class SchemaDto {

    String schemaName;
    String schemaOwner;

    public static SchemaDto mapRow(ResultSet resultSet, int i) throws SQLException {
        return new SchemaDto(resultSet.getString("schema_name"), resultSet.getString("schema_owner"));
    }

}

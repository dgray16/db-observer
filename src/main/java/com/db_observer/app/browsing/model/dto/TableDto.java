package com.db_observer.app.browsing.model.dto;

import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
public class TableDto {

    String tableName;
    String tableOwner;

    public static TableDto mapRow(ResultSet resultSet, int i) throws SQLException {
        return new TableDto(resultSet.getString("tablename"), resultSet.getString("tableowner"));
    }

}

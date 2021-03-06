package com.db_observer.app.browsing.model.dto;

import lombok.Value;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
public class ColumnDto {

    String columnName;
    String columnDefault;
    Boolean isNullable;
    String dataType;
    Integer characterMaximumLength;

    public static ColumnDto mapRow(ResultSet resultSet, int i) throws SQLException {
        return new ColumnDto(
                resultSet.getString("column_name"),
                resultSet.getString("column_default"),
                BooleanUtils.toBoolean(resultSet.getString("is_nullable")),
                resultSet.getString("data_type"),
                resultSet.getInt("character_maximum_length")
        );
    }

}

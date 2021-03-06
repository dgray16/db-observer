package com.db_observer.app.browsing.model.dto;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Data
public class TablePreviewDto {

    Map<String, String> columnsWithValues = new HashMap<>();

    public static TablePreviewDto mapRow(ResultSet resultSet, int i) throws SQLException {
        final TablePreviewDto result = new TablePreviewDto();

        for (int columnIterator = NumberUtils.INTEGER_ONE; columnIterator <= resultSet.getMetaData().getColumnCount(); columnIterator++) {
            result
                    .getColumnsWithValues()
                    .put(resultSet.getMetaData().getColumnName(columnIterator), resultSet.getString(columnIterator));
        }

        return result;
    }

}

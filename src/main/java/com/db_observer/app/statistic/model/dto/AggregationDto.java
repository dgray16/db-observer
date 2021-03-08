package com.db_observer.app.statistic.model.dto;

import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
public class AggregationDto {

    Integer min;
    Integer max;
    Double avg;
    Integer percentileCont;

    public static AggregationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AggregationDto(
                rs.getInt("min"), rs.getInt("max"), rs.getDouble("avg"), rs.getInt("percentile_cont")
        );
    }

}

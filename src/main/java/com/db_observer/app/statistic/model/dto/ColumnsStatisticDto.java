package com.db_observer.app.statistic.model.dto;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class ColumnsStatisticDto {

    Map<String, Integer> minValues = new HashMap<>();
    Map<String, Integer> maxValues = new HashMap<>();
    Map<String, Double> avgValues = new HashMap<>();
    Map<String, Integer> medianValues = new HashMap<>();

}

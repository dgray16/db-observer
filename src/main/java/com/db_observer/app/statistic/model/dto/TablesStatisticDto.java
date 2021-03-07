package com.db_observer.app.statistic.model.dto;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class TablesStatisticDto {

    Map<String, Long> records = new HashMap<>();
    Map<String, Integer> attributes = new HashMap<>();

}

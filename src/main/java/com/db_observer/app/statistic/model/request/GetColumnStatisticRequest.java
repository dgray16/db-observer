package com.db_observer.app.statistic.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetColumnStatisticRequest extends GetTablesStatisticRequest {

    @NotBlank
    String table;

}

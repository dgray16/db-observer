package com.db_observer.app.statistic.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetStatisticRequest {

    @NotNull
    @Positive
    Long connectionConfigId;

    @NotBlank
    String schema;

}

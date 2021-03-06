package com.db_observer.app.statistic.controller;

import com.db_observer.app.statistic.model.dto.ColumnsStatisticDto;
import com.db_observer.app.statistic.model.dto.TablesStatisticDto;
import com.db_observer.app.statistic.model.request.GetStatisticRequest;
import com.db_observer.app.statistic.service.StatisticWebService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticController {

    StatisticWebService statisticWebService;

    @GetMapping(value = "statistic/tables")
    public ResponseEntity<TablesStatisticDto> getTablesStatistic(@Valid @ModelAttribute GetStatisticRequest request) {
        return ResponseEntity.ok(statisticWebService.getTablesStatistic(request));
    }

    @GetMapping(value = "statistic/tables/{table}/columns")
    public ResponseEntity<ColumnsStatisticDto> getColumnStatistic(@PathVariable String table,
                                                                  @Valid @ModelAttribute GetStatisticRequest request) {

        return ResponseEntity.ok(statisticWebService.getColumnStatistic(table, request));
    }

}

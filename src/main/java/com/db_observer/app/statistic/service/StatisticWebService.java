package com.db_observer.app.statistic.service;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.common.DataSourceService;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import com.db_observer.app.statistic.model.dto.AggregationDto;
import com.db_observer.app.statistic.model.dto.ColumnsStatisticDto;
import com.db_observer.app.statistic.model.dto.TablesStatisticDto;
import com.db_observer.app.statistic.model.request.GetStatisticRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticWebService {

    ConnectionConfigService connectionConfigService;

    DataSourceService dataSourceService;

    @Transactional(readOnly = true)
    public ColumnsStatisticDto getColumnStatistic(String table, GetStatisticRequest request) {
        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(request.getConnectionConfigId());
        ColumnsStatisticDto result = new ColumnsStatisticDto();

        if (connectionConfig.isPresent()) {
            final DataSource dataSource = dataSourceService.getDataSource(connectionConfig.get(), request.getSchema());
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(dataSource);
            final List<TableDto> tables = dataSourceService.fetchTables(request.getSchema(), jdbcTemplate);

            final String sql = "SELECT MIN(%1$s), MAX(%1$s), AVG(%1$s)::numeric(10, 2), PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY %1$s) FROM "
                               + table
                               + ";";

            /* Try to avoid SQL injection */
            if (tables.stream().map(TableDto::getTableName).anyMatch(table::equals)) {
                final List<ColumnDto> columns = dataSourceService.fetchColumns(request.getSchema(), table, jdbcTemplate);

                columns
                        .stream()
                        .filter(this::isNumber)
                        .map(ColumnDto::getColumnName)
                        .forEach(name -> {
                            final String finalSql = String.format(sql, name);
                            final AggregationDto aggregation = jdbcTemplate.queryForObject(finalSql, Collections.emptyMap(), AggregationDto::mapRow);
                            result.getMaxValues().put(name, aggregation.getMax());
                            result.getMinValues().put(name, aggregation.getMin());
                            result.getAvgValues().put(name, aggregation.getAvg());
                            result.getMedianValues().put(name, aggregation.getPercentileCont());
                        });
            }
        }

        return result;
    }

    private boolean isNumber(ColumnDto columnDto) {
        return switch (columnDto.getDataType()) {
            case "smallint", "integer", "bigint", "decimal", "numeric", "real", "double precision", "smallserial", "serial", "bigserial" -> true;
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public TablesStatisticDto getTablesStatistic(GetStatisticRequest request) {
        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(request.getConnectionConfigId());
        TablesStatisticDto result = new TablesStatisticDto();

        if (connectionConfig.isPresent()) {
            final DataSource dataSource = dataSourceService.getDataSource(connectionConfig.get(), request.getSchema());
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(dataSource);
            final List<TableDto> tables = dataSourceService.fetchTables(request.getSchema(), jdbcTemplate);

            String sql = "SELECT reltuples::bigint AS estimate FROM pg_class where relname = :table;";

            tables
                    .stream()
                    .map(TableDto::getTableName)
                    .forEach(name -> {
                        final Long count = jdbcTemplate.queryForObject(sql, Map.of("table", name), Long.class);
                        result.getRecords().put(name, count);
                    });

            tables
                    .stream()
                    .map(TableDto::getTableName)
                    .forEach(name -> {
                        final List<ColumnDto> columns = dataSourceService.fetchColumns(request.getSchema(), name, jdbcTemplate);
                        result.getAttributes().put(name, columns.size());
                    });

        }

        return result;
    }

}

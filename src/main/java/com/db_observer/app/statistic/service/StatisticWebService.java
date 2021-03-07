package com.db_observer.app.statistic.service;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.common.DataSourceService;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import com.db_observer.app.statistic.model.dto.ColumnStatisticDto;
import com.db_observer.app.statistic.model.dto.TablesStatisticDto;
import com.db_observer.app.statistic.model.request.GetColumnStatisticRequest;
import com.db_observer.app.statistic.model.request.GetTablesStatisticRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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
    public ColumnStatisticDto getColumnStatistic(GetColumnStatisticRequest request) {
        return null;
    }

    @Transactional(readOnly = true)
    public TablesStatisticDto getTablesStatistic(GetTablesStatisticRequest request) {
        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(request.getConnectionConfigId());
        TablesStatisticDto result = new TablesStatisticDto();

        if (connectionConfig.isPresent()) {
            final DataSource dataSource = dataSourceService.getDataSource(connectionConfig.get(), request.getSchema());
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(dataSource);
            final List<TableDto> tables = dataSourceService.fetchTables(request.getSchema(), jdbcTemplate);

            String sql = "SELECT reltuples::bigint AS estimate FROM pg_class where relname=:table;";

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

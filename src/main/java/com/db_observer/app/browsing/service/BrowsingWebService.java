package com.db_observer.app.browsing.service;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.SchemaDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.browsing.model.dto.TablePreviewDto;
import com.db_observer.app.common.DataSourceService;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrowsingWebService {

    ConnectionConfigService connectionConfigService;

    DataSourceService dataSourceService;

    @Transactional(readOnly = true)
    public List<SchemaDto> getSchemas(Long connectionConfigId) {
        List<SchemaDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> foundConnection = connectionConfigService.findById(connectionConfigId);

        if (foundConnection.isPresent()) {
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(foundConnection.get());
            result = jdbcTemplate
                    .queryForStream("SELECT schema_name, schema_owner FROM information_schema.schemata;", Collections.emptyMap(), SchemaDto::mapRow)
                    .collect(Collectors.toUnmodifiableList());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<TableDto> getTables(Long connectionConfigId, String schema) {
        List<TableDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(connectionConfig.get());
            result = dataSourceService.fetchTables(schema, jdbcTemplate);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ColumnDto> getColumns(Long connectionConfigId, String schema, String table) {
        List<ColumnDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            result = dataSourceService.fetchColumns(schema, table, dataSourceService.getJdbcTemplate(connectionConfig.get()));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<TablePreviewDto> getTablePreview(Long connectionConfigId, String schema, String table) {
        List<TablePreviewDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            final DataSource dataSource = dataSourceService.getDataSource(connectionConfig.get(), schema);
            final NamedParameterJdbcTemplate jdbcTemplate = dataSourceService.getJdbcTemplate(dataSource);
            final List<TableDto> tables = dataSourceService.fetchTables(schema, jdbcTemplate);

            /* Try to avoid SQL injection */
            if (tables.stream().map(TableDto::getTableName).anyMatch(table::equals)) {
                final List<ColumnDto> columns = dataSourceService.fetchColumns(schema, table, jdbcTemplate);

                if (CollectionUtils.isNotEmpty(columns)) {
                    final List<String> columnsAsString = columns.stream().map(ColumnDto::getColumnName).collect(Collectors.toUnmodifiableList());
                    final String selectColumns = String.join(", ", columnsAsString);

                    final String sql = String.format("SELECT %s FROM %s", selectColumns, table);

                    result = jdbcTemplate
                            .queryForStream(sql, Collections.emptyMap(), TablePreviewDto::mapRow)
                            .collect(Collectors.toUnmodifiableList());
                }
            }
        }
        return result;
    }

}

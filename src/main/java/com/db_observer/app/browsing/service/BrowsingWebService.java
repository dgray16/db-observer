package com.db_observer.app.browsing.service;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.SchemaDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.browsing.model.dto.TablePreviewDto;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import com.db_observer.app.domain.service.ConnectionConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrowsingWebService {

    ConnectionConfigService connectionConfigService;

    @Transactional(readOnly = true)
    public List<SchemaDto> getSchemas(Long connectionConfigId) {
        List<SchemaDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> foundConnection = connectionConfigService.findById(connectionConfigId);

        if (foundConnection.isPresent()) {
            final NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplate(getDataSource(foundConnection.get()));
            /* TODO support multiple databases */
            result = jdbcTemplate
                    .queryForStream("SELECT schema_name, schema_owner FROM information_schema.schemata;", Collections.emptyMap(), SchemaDto::mapRow)
                    .collect(Collectors.toUnmodifiableList());
        }

        return result;
    }

    private DataSource getDataSource(ConnectionConfig connectionConfig) {
        return getDataSource(connectionConfig, null);
    }

    private DataSource getDataSource(ConnectionConfig connectionConfig, String schema) {
        /* TODO support multiple databases */
        final String url = String.format(
                "jdbc:postgresql://%s:%d/%s",
                connectionConfig.getDatabaseHostname(),
                connectionConfig.getDatabasePort(),
                connectionConfig.getDatabaseName()
        );

        final SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
                url, connectionConfig.getUsername(), connectionConfig.getPassword(), false
        );

        if (StringUtils.isNotBlank(schema)) {
            dataSource.setSchema(schema);
        }

        return dataSource;
    }

    private NamedParameterJdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Transactional(readOnly = true)
    public List<TableDto> getTables(Long connectionConfigId, String schema) {
        List<TableDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            final NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplate(getDataSource(connectionConfig.get()));

            /* TODO support multiple databases */
            result = fetchTables(schema, jdbcTemplate);
        }

        return result;
    }

    private List<TableDto> fetchTables(String schema, NamedParameterJdbcTemplate jdbcTemplate) {
        final String sql = "SELECT tablename, tableowner FROM pg_catalog.pg_tables WHERE schemaname = :schema;";
        return jdbcTemplate
                .queryForStream(sql, Map.of("schema", schema), TableDto::mapRow)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public List<ColumnDto> getColumns(Long connectionConfigId, String schema, String table) {
        List<ColumnDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            result = fetchColumns(schema, table, getJdbcTemplate(getDataSource(connectionConfig.get())));
        }

        return result;
    }

    private List<ColumnDto> fetchColumns(String schema, String table, NamedParameterJdbcTemplate jdbcTemplate) {
        final Map<String, String> params = Map.of("schema", schema, "table", table);

        final String query =
            """
            SELECT column_name, column_default, is_nullable, data_type, character_maximum_length
            FROM information_schema.columns 
            WHERE table_schema = :schema AND table_name = :table;
            """;

        return jdbcTemplate
                .queryForStream(query, params, ColumnDto::mapRow)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public List<TablePreviewDto> getTablePreview(Long connectionConfigId, String schema, String table) {
        List<TablePreviewDto> result = Collections.emptyList();

        final Optional<ConnectionConfig> connectionConfig = connectionConfigService.findById(connectionConfigId);

        if (connectionConfig.isPresent()) {
            final NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplate(getDataSource(connectionConfig.get(), schema));
            final List<TableDto> tables = fetchTables(schema, jdbcTemplate);

            /* Try to avoid SQL injection */
            if (tables.stream().map(TableDto::getTableName).anyMatch(table::equals)) {
                final List<ColumnDto> columns = fetchColumns(schema, table, jdbcTemplate);

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

package com.db_observer.app.common;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.domain.model.entity.ConnectionConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataSourceService {

    private DataSource getDataSource(ConnectionConfig connectionConfig) {
        return getDataSource(connectionConfig, null);
    }

    public DataSource getDataSource(ConnectionConfig connectionConfig, String schema) {
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

    public NamedParameterJdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    public NamedParameterJdbcTemplate getJdbcTemplate(ConnectionConfig connectionConfig) {
        return getJdbcTemplate(getDataSource(connectionConfig));
    }

    public List<TableDto> fetchTables(String schema, NamedParameterJdbcTemplate jdbcTemplate) {
        final String sql = "SELECT tablename, tableowner FROM pg_catalog.pg_tables WHERE schemaname = :schema;";
        return jdbcTemplate
                .queryForStream(sql, Map.of("schema", schema), TableDto::mapRow)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ColumnDto> fetchColumns(String schema, String table, NamedParameterJdbcTemplate jdbcTemplate) {
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

}

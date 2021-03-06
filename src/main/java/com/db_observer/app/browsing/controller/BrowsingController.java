package com.db_observer.app.browsing.controller;

import com.db_observer.app.browsing.model.dto.ColumnDto;
import com.db_observer.app.browsing.model.dto.SchemaDto;
import com.db_observer.app.browsing.model.dto.TableDto;
import com.db_observer.app.browsing.model.dto.TablePreviewDto;
import com.db_observer.app.browsing.service.BrowsingWebService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrowsingController {

    BrowsingWebService browsingWebService;

    @GetMapping(value = "browsing/schemas")
    public ResponseEntity<List<SchemaDto>> getSchemas(@Valid @RequestParam @Positive Long connectionConfigId) {
        return ResponseEntity.ok(browsingWebService.getSchemas(connectionConfigId));
    }

    @GetMapping(value = "browsing/schemas/{schema}/tables")
    public ResponseEntity<List<TableDto>> getTables(@Valid @PathVariable @NotBlank String schema,
                                                    @Valid @RequestParam @Positive Long connectionConfigId) {

        return ResponseEntity.ok(browsingWebService.getTables(connectionConfigId, schema));
    }

    @GetMapping(value = "browsing/schemas/{schema}/tables/{table}")
    public ResponseEntity<List<ColumnDto>> getColumns(@Valid @PathVariable @NotBlank String schema,
                                                      @Valid @PathVariable @NotBlank String table,
                                                      @Valid @RequestParam @Positive Long connectionConfigId) {

        return ResponseEntity.ok(browsingWebService.getColumns(connectionConfigId, schema, table));
    }

    @GetMapping(value = "browsing/schemas/{schema}/tables/{table}/preview")
    public ResponseEntity<List<TablePreviewDto>> getTablePreview(@Valid @PathVariable @NotBlank String schema,
                                                           @Valid @PathVariable @NotBlank String table,
                                                           @Valid @RequestParam @Positive Long connectionConfigId) {

        return ResponseEntity.ok(browsingWebService.getTablePreview(connectionConfigId, schema, table));
    }

}

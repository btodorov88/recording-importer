package com.softavail.recordingimporter.controller.dto;

import com.softavail.recordingimporter.model.ImportStatus;
import lombok.Data;

/**
 * Represents the result of the import HTTP service
 */
@Data
public class ImportStatusDto {
    public ImportStatusDto(ImportStatus status) {
        this.importStartedAt = status.getStarted().getTime();
    }

    private Long importStartedAt;
}

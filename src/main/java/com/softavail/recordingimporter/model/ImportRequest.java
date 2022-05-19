package com.softavail.recordingimporter.model;

import lombok.Builder;
import lombok.Data;

/**
 * Input data needed in order to trigger file import
 */
@Data
@Builder
public class ImportRequest {
    private String filename;
    private String callId;
    private String from;
    private String to;
    private long started;
    private long duration;
}

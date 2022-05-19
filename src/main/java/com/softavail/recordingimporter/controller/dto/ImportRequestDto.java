package com.softavail.recordingimporter.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Represents the input data for the import HTTP service
 */
@Data
public class ImportRequestDto {
    @NotBlank(message = "Filename is mandatory")
    private String filename;
    private String callId;
    private String from;
    private String to;
    private long started;
    private long duration;
}

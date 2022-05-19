package com.softavail.recordingimporter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Represents the result of a file import
 */
@Data
@AllArgsConstructor
public class ImportStatus {
    private Date started;
}

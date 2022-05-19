package com.softavail.recordingimporter.exceptions;

/**
 * Signals that the import operation has failed.
 */
public class ImportFailedException extends RuntimeException {
    public ImportFailedException(String message) {
        super(message);
    }

    public ImportFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

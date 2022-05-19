package com.softavail.recordingimporter.exceptions;


import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Level;

/**
 * Provides custom logic for handling exceptions in the controllers.
 */
@ControllerAdvice
@Log
public class CustomExceptionHandler {
    @ExceptionHandler(value = ImportFailedException.class)
    public ResponseEntity<Object> exception(ImportFailedException exception) {
        log.log(Level.SEVERE, "Error in controller", exception);
        // Hide any internal info from the clients
        return ResponseEntity.internalServerError().body("Media file import failed. Please contact an administrator");
    }
}

package com.softavail.recordingimporter.service;

import com.softavail.recordingimporter.clients.ProcessorClient;
import com.softavail.recordingimporter.exceptions.ImportFailedException;
import com.softavail.recordingimporter.model.ImportRequest;
import com.softavail.recordingimporter.model.ImportStatus;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;

/**
 * Service responsible for triggering media file imports
 */
@Service
@Log
public class ImportService {
    public final ContentType CONTENT_TYPE_OGG = ContentType.create("video/ogg");

    @Autowired
    private ProcessorClient processorClient;

    public ImportStatus startImport(ImportRequest request) {
        String filename = request.getFilename();
        try (InputStream mediaFileStream = new FileInputStream(filename)) {
            processorClient.postMediaFile(filename, CONTENT_TYPE_OGG, mediaFileStream, request);
            log.info("Media file import triggered successfully");
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Processor request failed", ex);
            throw new ImportFailedException("Cannot trigger media file import", ex);
        }

        Date startedAt = new Date();
        return new ImportStatus(startedAt);
    }
}

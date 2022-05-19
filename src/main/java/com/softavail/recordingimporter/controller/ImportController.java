package com.softavail.recordingimporter.controller;

import com.softavail.recordingimporter.controller.dto.ImportRequestDto;
import com.softavail.recordingimporter.controller.dto.ImportStatusDto;
import com.softavail.recordingimporter.model.ImportRequest;
import com.softavail.recordingimporter.model.ImportStatus;
import com.softavail.recordingimporter.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ImportController {
    @Autowired
    private ImportService importService;

    /**
     * Triggers new media file import
     */
    @PostMapping("/imports")
    public ImportStatusDto createImport(@Valid @RequestBody ImportRequestDto importRequestDto) {
        ImportRequest request = createImportRequest(importRequestDto);
        ImportStatus result = importService.startImport(request);
        return new ImportStatusDto(result);
    }

    private ImportRequest createImportRequest(ImportRequestDto importRequestDto) {
        // This can be simplified using object mapping
        ImportRequest request = ImportRequest.builder()
                .filename(importRequestDto.getFilename())
                .started(importRequestDto.getStarted())
                .callId(importRequestDto.getCallId())
                .duration(importRequestDto.getDuration())
                .from(importRequestDto.getFrom())
                .to(importRequestDto.getTo())
                .build();
        return request;
    }
}

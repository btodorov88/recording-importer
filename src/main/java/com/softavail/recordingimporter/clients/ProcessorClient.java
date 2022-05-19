package com.softavail.recordingimporter.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Client component facilitating the communication with the processor service.
 */
@Component
public class ProcessorClient {
    public static final String METADATA_PART_NAME = "metadata";
    public static final String MEDIA_FILE_PART_NAME = "mediaFile";
    public static final String PUBLISH_RESOURCE = "/publish";

    @Value("${processor.endpoint}")
    private String processorEndpoint;

    public void postMediaFile(String fileName, ContentType contentType, InputStream stream, Object metadata) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(processorEndpoint + PUBLISH_RESOURCE);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(METADATA_PART_NAME, new ObjectMapper().writeValueAsString(metadata), ContentType.APPLICATION_JSON);

        builder.addBinaryBody(
                MEDIA_FILE_PART_NAME,
                stream,
                contentType,
                fileName
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);

        // Check if status is 2xx
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() / 100 != 2) {
            throw new IOException("Processor request failed: " + statusLine.getReasonPhrase());
        }
    }
}

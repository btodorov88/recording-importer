package com.softavail.recordingimporter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.softavail.recordingimporter.controller.dto.ImportRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Recording importer service
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RecordingImporterIntegrationTests {

    private static final String TEST_DATA = "Some test data...";

    // The port where the test http server will run
    private static final int wireMockPort = 8484;

    private static final WireMockServer wireMockServer = new WireMockServer(wireMockPort);
    private static File testFile;

    @LocalServerPort
    private int testServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("processor.endpoint", () -> "http://localhost:" + wireMockPort);
    }

    @BeforeAll
    public static void setup() throws IOException {
        testFile = File.createTempFile("test", ".ogg");
        try (PrintWriter out = new PrintWriter(testFile)) {
            out.println(TEST_DATA);
        }

        wireMockServer.start();
        configureFor("localhost", wireMockPort);
    }

    @AfterAll
    public static void cleanup() {
        wireMockServer.stop();
        testFile.delete();
    }

    @BeforeEach
    public void prepareTest() {
        WireMock.reset();
    }

    @Test
    public void testImportPositive() {
        stubFor(post(urlEqualTo("/publish")).willReturn(aResponse().withStatus(200)));

        ImportRequestDto request = new ImportRequestDto();
        request.setFilename(testFile.getAbsolutePath());

        HttpEntity<ImportRequestDto> requestEntity = new HttpEntity<>(request);
        assertThat(this.restTemplate.exchange("http://localhost:" + testServerPort + "/imports", HttpMethod.POST, requestEntity,
                String.class).getStatusCode()).isEqualTo(HttpStatus.OK);

        // may need more sophisticated method for the body validation
        verify(postRequestedFor(urlEqualTo("/publish"))
                .withRequestBody(containing("form-data; name=\"metadata\"\r\nContent-Type: application/json"))
                .withRequestBody(containing("form-data; name=\"mediaFile\"; filename=\"" + testFile.getAbsolutePath() + "\"\r\nContent-Type: video/ogg"))
                .withRequestBody(containing(TEST_DATA))
        );
    }

    /**
     * Simulates the situation when the external processor service is not operational
     */
    @Test
    public void testImportFailedProcessor() {
        stubFor(post(urlEqualTo("/publish")).willReturn(aResponse().withStatus(500)));

        ImportRequestDto request = new ImportRequestDto();
        request.setFilename(testFile.getAbsolutePath());

        HttpEntity<ImportRequestDto> requestEntity = new HttpEntity<>(request);
        assertThat(this.restTemplate.exchange("http://localhost:" + testServerPort + "/imports", HttpMethod.POST, requestEntity,
                String.class).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Trigger import with invalid input params
     */
    @Test
    public void testImportNoFilename() {
        ImportRequestDto request = new ImportRequestDto();

        HttpEntity<ImportRequestDto> requestEntity = new HttpEntity<>(request);
        assertThat(this.restTemplate.exchange("http://localhost:" + testServerPort + "/imports", HttpMethod.POST, requestEntity,
                String.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
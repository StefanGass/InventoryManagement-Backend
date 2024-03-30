package net.inventorymanagement.inventorymanagementwebservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
class InventoryManagementWebserviceApplicationTests {

    @Value("${path.to.resources.folder}")
    private String pathToResourcesFolder;

    @Value("${path.to.test.resources.folder}")
    private String pathToTestResourcesFolder;

    @Test
    void contextLoads() {
    }

    @Test
    void tryToImportPythonPackages() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", pathToTestResourcesFolder + "test_imports.py");
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        assertEquals("No errors should be detected", 0, exitCode);
    }

    @Test
    void tryToProcessPythonScript() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", pathToResourcesFolder+ "generate_qr.py", "-1", "TEST-2022-0001");
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());
        //results.remove(0);  // only needed if payload gets printed
        String result = String.join(", ", results);

        assertThat("Results should be empty", results, is(empty()));
        assertEquals("Result should contain the following output", "", result);

        int exitCode = process.waitFor();
        assertEquals("No errors should be detected", 0, exitCode);
    }

    List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }

}

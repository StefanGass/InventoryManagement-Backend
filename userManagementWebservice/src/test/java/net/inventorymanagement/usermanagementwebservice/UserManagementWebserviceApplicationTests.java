package net.inventorymanagement.usermanagementwebservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class UserManagementWebserviceApplicationTests {

    @Value("${path.to.active-directory-binding-pwd}")
    private String pathToActiveDirectoryBindingPwdCsv;

    @Test
    void contextLoads() {
    }

    @Test
    void testFileContainsString() throws IOException {
        String filePath = pathToActiveDirectoryBindingPwdCsv;

        File file = new File(filePath);
        assertTrue(file.exists());

        List<String> fileContent = Files.readAllLines(Paths.get(filePath));

        if (!fileContent.get(0).isBlank()) {
            assertTrue(true);
        } else {
            fail();
        }
    }

}

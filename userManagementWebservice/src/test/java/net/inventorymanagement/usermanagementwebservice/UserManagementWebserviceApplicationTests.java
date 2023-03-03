package net.inventorymanagement.usermanagementwebservice;

import net.inventorymanagement.usermanagementwebservice.utils.FileReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserManagementWebserviceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void checkFileReader() {
        List<String> userExceptionsList = new ArrayList<>();
        FileReader fileReader = FileReader.getInstance();
        fileReader.loadFile(userExceptionsList);
        assertEquals("username;firstName_lastName;mailAddress;", userExceptionsList.get(0));
    }

}

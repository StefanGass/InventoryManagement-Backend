package net.inventorymanagement.usermanagementwebservice.utils;

import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * File reader, reads files.
 */

@Getter
public class FileReader {

    private static FileReader instance;

    // singleton design pattern
    public static FileReader getInstance() {
        if (instance == null) {
            instance = new FileReader();
        }
        return instance;
    }

    private FileReader() {
    }

    public void loadFile(List<String> userExceptionsList) {
        Path inputPath = Paths.get("src", "main", "resources", "user_exceptions.csv");
        getInput(userExceptionsList, inputPath);
    }

    private void getInput(List<String> userExceptionsList, Path inputPath) {
        List<String> input;
        try {
            input = Files.readAllLines(inputPath);
            for (int i = 3; i < input.size(); i++) {
                userExceptionsList.add(input.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The input file does not exist.");
        }
    }

}

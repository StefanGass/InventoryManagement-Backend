package net.inventorymanagement.inventorymanagementwebservice.utils;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import net.inventorymanagement.inventorymanagementwebservice.model.Picture;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Python executor, executes python3 scripts.
 */

@Getter
@Log4j2
public class PythonExecutor {

    public static void generateQrCodeWithPythonScript(String pathToResourcesFolder, Integer inventoryId, String inventoryInternalNumber) {
        try {
            getPythonOutput(new ProcessBuilder("python3", pathToResourcesFolder + "generate_qr.py", inventoryId.toString(), inventoryInternalNumber));
        } catch (Exception e) {
            System.out.println("Unable to execute python script 'generate_qr.py' on input: " + inventoryId + ", " + inventoryInternalNumber);
            e.printStackTrace();
        }
    }

    public static void printQrCodeWithPythonScript(String pathToResourcesFolder, Integer inventoryId, String printerIp, String printerModel, String printCounter) {
        try {
            getPythonOutput(new ProcessBuilder("python3", pathToResourcesFolder + "print_qr.py", inventoryId.toString(), printerIp, printerModel, printCounter));
        } catch (Exception e) {
            System.out.println("Unable to execute python script 'print_qr.py' on input: " + inventoryId + ", " + printerIp + ", " + printerModel + ", " + printCounter);
            e.printStackTrace();
        }
    }

    public static String generateTransferProtocolWithPythonScript(String pathToResourcesFolder, InventoryItem item, Picture picture, String userName) {
        try {
            getPythonOutput(new ProcessBuilder("python3", pathToResourcesFolder + "generate_transfer_protocol.py", item.getId().toString(), item.getIssuedTo(), item.getItemInternalNumber(), item.getType().getCategory().getCategoryName(), item.getType().getTypeName(), item.getItemName(), item.getSerialNumber(), item.getLocation().getLocationName(), userName, picture.getPictureUrl()));
            return getBase64StringFromPdf(pathToResourcesFolder);
        } catch (Exception e) {
            System.out.println("Unable to execute python script 'generate_transfer_protocol.py' on input: " + item.toString() + ", initiated from user :" + userName);
            e.printStackTrace();
            return null;
        }
    }

    private static String getBase64StringFromPdf(String pathToResourcesFolder) {
        try {
            File file = new File(pathToResourcesFolder + "temp_protocol.pdf");
            byte[] bytes = Files.readAllBytes(file.toPath());
            String b64 = Base64.getEncoder().encodeToString(bytes);
            boolean fileRemoved = file.delete();
            if (!fileRemoved) {
                throw new Exception("File not found.");
            } else {
                return "data:application/pdf;base64," + b64;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void getPythonOutput(ProcessBuilder processBuilder) throws Exception {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        if (process.waitFor() != 0) {
            throw new Exception(String.valueOf(readPythonProcessOutput(process.getInputStream())));
        }
        readPythonProcessOutput(process.getInputStream());
    }

    private static List<String> readPythonProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }

}

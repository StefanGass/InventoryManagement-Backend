package net.inventorymanagement.facades;

import net.inventorymanagement.dtos.DetailInventoryItemDTO;
import net.inventorymanagement.dtos.InventoryItemDTO;
import net.inventorymanagement.model.*;
import net.inventorymanagement.service.InventoryManagementService;
import net.inventorymanagement.utils.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class InventoryItemFacade {

    @Autowired
    private InventoryManagementService inventoryManagementService;

    public InventoryItemDTO mapModelToDTO(InventoryItem model) {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setId(model.getId());
        inventoryItemDTO.setType(model.getType());
        inventoryItemDTO.setItemInternalNumber(model.getItemInternalNumber());
        inventoryItemDTO.setItemName(model.getItemName());
        inventoryItemDTO.setSerialNumber(model.getSerialNumber());
        inventoryItemDTO.setSupplier(model.getSupplier());
        inventoryItemDTO.setLocation(model.getLocation());
        inventoryItemDTO.setPieces(model.getPieces());
        inventoryItemDTO.setPiecesStored(model.getPiecesStored());
        inventoryItemDTO.setPiecesIssued(model.getPiecesIssued());
        inventoryItemDTO.setPiecesDropped(model.getPiecesDropped());
        inventoryItemDTO.setIssuedTo(model.getIssuedTo());
        inventoryItemDTO.setDeliveryDate(model.getDeliveryDate());
        inventoryItemDTO.setIssueDate(model.getIssueDate());
        inventoryItemDTO.setDroppingDate(model.getDroppingDate());
        inventoryItemDTO.setStatus(model.getStatus());
        inventoryItemDTO.setDepartment(model.getDepartment());
        model.getChange().stream().max(Comparator.comparing(Change::getChangeDate)).ifPresent(lastChange -> inventoryItemDTO.setLastChangedDate(lastChange.getChangeDate()));
        inventoryItemDTO.setActive(model.isActive());
        return inventoryItemDTO;
    }

    public DetailInventoryItemDTO mapModelToDetailDTO(InventoryItem model) {
        InventoryItemDTO item = mapModelToDTO(model);
        List<Picture> base64List = parseBase64(model.getPictures());
        return new DetailInventoryItemDTO(item, model.getDroppingReason(), model.getComments(), base64List, model.getChange());
    }

    public InventoryItem mapDTOToModel(DetailInventoryItemDTO inventoryItemDTO, InventoryItem savedModel) {
        Location location = inventoryManagementService.getLocationByName(inventoryItemDTO.getLocation().getLocationName());
        Supplier supplier = null;
        if (inventoryItemDTO.getSupplier() != null) {
            supplier = inventoryManagementService.getSupplierByName(inventoryItemDTO.getSupplier().getSupplierName());
        }
        Type type = inventoryManagementService.getTypeByName(inventoryItemDTO.getType().getTypeName());
        InventoryItem inventoryItem;
        if (savedModel != null) {
            inventoryItem = savedModel;
        } else {
            inventoryItem = new InventoryItem();
        }
        Department department = inventoryManagementService.getDepartmentByName(inventoryItemDTO.getDepartment().getDepartmentName());
        inventoryItem.setItemInternalNumber(inventoryItemDTO.getItemInternalNumber());
        inventoryItem.setType(type);
        inventoryItem.setItemName(inventoryItemDTO.getItemName());
        inventoryItem.setSerialNumber(inventoryItemDTO.getSerialNumber());
        inventoryItem.setSupplier(supplier);
        inventoryItem.setLocation(location);
        inventoryItem.setDepartment(department);
        inventoryItem.setPieces(inventoryItemDTO.getPieces());
        inventoryItem.setPiecesStored(inventoryItemDTO.getPiecesStored());
        inventoryItem.setPiecesIssued(inventoryItemDTO.getPiecesIssued());
        inventoryItem.setPiecesDropped(inventoryItemDTO.getPiecesDropped());
        inventoryItem.setIssuedTo(inventoryItemDTO.getIssuedTo());
        inventoryItem.setDeliveryDate(inventoryItemDTO.getDeliveryDate());
        inventoryItem.setIssueDate(inventoryItemDTO.getIssueDate());
        inventoryItem.setDroppingDate(inventoryItemDTO.getDroppingDate());
        inventoryItem.setDroppingReason(inventoryItemDTO.getDroppingReason());
        inventoryItem.setComments(inventoryItemDTO.getComments());
        inventoryItem.setStatus(createStatus(inventoryItemDTO));
        inventoryItem.setActive(true);
        return inventoryItem;
    }

    private String createStatus(InventoryItemDTO item) {
        Integer pieces = item.getPieces();
        Integer piecesDropped = item.getPiecesDropped();
        Integer piecesIssued = item.getPiecesIssued();
        Integer piecesStored = item.getPiecesStored();

        if (Objects.equals(pieces, piecesDropped)) {
            return StatusEnum.DROPPED.name();
        } else if (Objects.equals(pieces, piecesIssued)) {
            return StatusEnum.ISSUED.name();
        } else if (Objects.equals(pieces, piecesStored)) {
            return StatusEnum.IN_STORE.name();
        } else {
            return StatusEnum.SPREAD.name();
        }
    }

    public void savePictures(List<Picture> pictures, InventoryItem item) throws IOException {
        if (pictures != null && pictures.size() > 0) {
            for (int i = 0; i < pictures.size(); i++) {
                Picture current = pictures.get(i);
                String[] splitUrl = current.getPictureUrl().split(",");
                String toDecode = splitUrl[1];
                String fileEnding = getFileType(splitUrl[0]);
                byte[] decodedImg = Base64.getDecoder()
                        .decode(toDecode.getBytes(StandardCharsets.UTF_8));
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                String destinationFolder = "inventoryManagementWebservice/src/main/resources/pictures/" +
                        item.getItemInternalNumber() + "/";

                String destinationFile = destinationFolder + dtf.format(now) + "-" + i + "." + fileEnding;
                current.setPictureUrl(destinationFile);
                current.setInventoryItem(item);
                Files.createDirectories(Path.of(destinationFolder));
                File f = new File(destinationFile);
                System.out.println(f);

                if (fileEnding.equals("pdf")) {
                    try (FileOutputStream fos = new FileOutputStream(f)) {
                        fos.write(decodedImg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        inventoryManagementService.addPicture(current);
                    }
                } else {
                    try {
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedImg));
                        ImageIO.write(img, fileEnding, f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        inventoryManagementService.addPicture(current);
                    }
                }
            }
        }
    }

    private String getFileType(String fileEnding) {
        if (fileEnding.contains("jpg") || fileEnding.contains("jpeg")) {
            return "jpg";
        } else if (fileEnding.contains("png")) {
            return "png";
        } else if (fileEnding.contains("gif")) {
            return "gif";
        } else if (fileEnding.contains("pdf")) {
            return "pdf";
        } else {
            return "bmp";
        }
    }

    public List<Picture> parseBase64(List<Picture> pictures) {
        return pictures.stream().map(picture -> {
            File f = new File(picture.getPictureUrl());
            String fileType = getFileType(picture.getPictureUrl());
            String imageString;
            if (fileType.equals("pdf")) {
                imageString = "data:application/pdf;base64,";
            } else {
                imageString = "data:image/" + fileType + ";base64,";
            }

            try (FileInputStream fileInputStreamReader = new FileInputStream(f)) {

                // get byte array from image stream
                int bufLength = 2048;
                byte[] buffer = new byte[2048];
                byte[] data;

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int readLength;
                while ((readLength = fileInputStreamReader.read(buffer, 0, bufLength)) != -1) {
                    out.write(buffer, 0, readLength);
                }

                data = out.toByteArray();
                imageString += Base64.getEncoder().withoutPadding().encodeToString(data);

                out.close();
                fileInputStreamReader.close();
                System.out.println("Encode Image Result : " + imageString);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Picture pictureFinal = new Picture();
            pictureFinal.setPictureUrl(imageString);
            pictureFinal.setInventoryItem(picture.getInventoryItem());
            pictureFinal.setId(picture.getId());
            return pictureFinal;
        }).toList();
    }

}

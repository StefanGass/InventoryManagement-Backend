package net.inventorymanagement.inventorymanagementwebservice.facades;

import net.inventorymanagement.inventorymanagementwebservice.dtos.DetailInventoryItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.dtos.InventoryItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import net.inventorymanagement.inventorymanagementwebservice.service.InventoryManagementService;
import net.inventorymanagement.inventorymanagementwebservice.utils.StatusEnum;
import net.inventorymanagement.inventorymanagementwebservice.utils.ThumbnailGenerator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class InventoryItemFacade {

    @Value("${path.to.documents.folder}")
    private String pathToDocumentsFolder;

    @Value("${path.to.pictures.folder}")
    private String pathToPicturesFolder;

    @Value("${path.to.thumbnails.folder}")
    private String pathToThumbnailsFolder;

    private final InventoryManagementService inventoryManagementService;

    @Autowired
    public InventoryItemFacade(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    public InventoryItemDTO mapModelToDTO(InventoryItem model) {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setId(model.getId());
        inventoryItemDTO.setType(model.getType());
        inventoryItemDTO.setItemInternalNumber(model.getItemInternalNumber());
        inventoryItemDTO.setItemName(model.getItemName());
        inventoryItemDTO.setSerialNumber(model.getSerialNumber());
        inventoryItemDTO.setWarrantyEndDate(model.getWarrantyEndDate());
        inventoryItemDTO.setSupplier(model.getSupplier());
        inventoryItemDTO.setLocation(model.getLocation());
        inventoryItemDTO.setRoom(model.getRoom());
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
        inventoryItemDTO.setDroppingQueue(model.getDroppingQueue());
        if (model.getChange() != null) {
            inventoryItemDTO.setCreationDate(model.getFirstChange().getChangeDate());
            inventoryItemDTO.setLastChangedDate(model.getLastChange().getChangeDate());
        }
        inventoryItemDTO.setActive(model.isActive());
        inventoryItemDTO.setOldItemNumber(model.getOldItemNumber());
        return inventoryItemDTO;
    }

    public DetailInventoryItemDTO mapModelToDetailDTO(InventoryItem model) {
        InventoryItemDTO item = mapModelToDTO(model);
        List<Picture> base64List = parseBase64WithThumbnail(model.getPictures());
        return new DetailInventoryItemDTO(item, model.getDroppingReason(), model.getComments(),
                base64List, model.getChange(), model.getDroppingQueuePieces(),
                model.getDroppingQueueReason(), model.getDroppingQueueRequester(),
                model.getDroppingQueueDate());
    }

    public InventoryItem mapDTOToModel(DetailInventoryItemDTO inventoryItemDTO, InventoryItem savedModel) {
        Location location = inventoryManagementService.getLocationByName(inventoryItemDTO.getLocation().getLocationName());
        Supplier supplier = null;
        if (inventoryItemDTO.getSupplier() != null) {
            supplier = inventoryManagementService.getSupplierByName(inventoryItemDTO.getSupplier().getSupplierName());
        }
        Type type = inventoryManagementService.getTypeByName(inventoryItemDTO.getType().getTypeName());
        InventoryItem inventoryItem;
        inventoryItem = Objects.requireNonNullElseGet(savedModel, InventoryItem::new);
        Department department = inventoryManagementService.getDepartmentByName(inventoryItemDTO.getDepartment().getDepartmentName());
        inventoryItem.setItemInternalNumber(inventoryItemDTO.getItemInternalNumber());
        inventoryItem.setType(type);
        inventoryItem.setItemName(inventoryItemDTO.getItemName());
        inventoryItem.setSerialNumber(inventoryItemDTO.getSerialNumber());
        inventoryItem.setWarrantyEndDate(inventoryItemDTO.getWarrantyEndDate());
        inventoryItem.setSupplier(supplier);
        inventoryItem.setLocation(location);
        inventoryItem.setRoom(inventoryItemDTO.getRoom());
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
        inventoryItem.setOldItemNumber(inventoryItemDTO.getOldItemNumber());
        inventoryItem.setDroppingQueue(inventoryItemDTO.getDroppingQueue());
        inventoryItem.setDroppingQueueDate(inventoryItemDTO.getDroppingQueueDate());
        inventoryItem.setDroppingQueuePieces(inventoryItemDTO.getDroppingQueuePieces());
        inventoryItem.setDroppingQueueReason(inventoryItemDTO.getDroppingQueueReason());
        inventoryItem.setDroppingQueueRequester(inventoryItemDTO.getDroppingQueueRequester());
        return inventoryItem;
    }

    private String createStatus(InventoryItemDTO item) {
        boolean active = item.isActive();
        Integer pieces = item.getPieces();
        Integer piecesDropped = item.getPiecesDropped();
        Integer piecesIssued = item.getPiecesIssued();
        Integer piecesStored = item.getPiecesStored();

        if (!active) {
            return StatusEnum.DEAKTIVIERT.name();
        } else if (Objects.equals(pieces, piecesDropped)) {
            return StatusEnum.AUSGESCHIEDEN.name();
        } else if (Objects.equals(pieces, piecesIssued)) {
            return StatusEnum.AUSGEGEBEN.name();
        } else if (Objects.equals(pieces, piecesStored)) {
            return StatusEnum.LAGERND.name();
        } else {
            return StatusEnum.VERTEILT.name();
        }
    }

    public void savePictures(List<Picture> pictures, InventoryItem item, boolean transferProtocol) throws IOException {
        if (pictures != null && !pictures.isEmpty()) {
            for (int i = 0; i < pictures.size(); i++) {
                Picture current = pictures.get(i);
                String[] splitUrl = current.getPictureUrl().split(",");
                String toDecode = splitUrl[1];
                String fileEnding = getFileType(splitUrl[0]);
                byte[] decodedImg = Base64.getDecoder()
                        .decode(toDecode.getBytes(StandardCharsets.UTF_8));
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                String destinationFolder;
                if (fileEnding.equals("pdf")) {
                    destinationFolder = pathToDocumentsFolder + item.getItemInternalNumber() + "/";
                } else {
                    destinationFolder = pathToPicturesFolder + item.getItemInternalNumber() + "/";
                }
                String destinationFile;
                if (transferProtocol) {
                    destinationFile = destinationFolder + dtf.format(now) + "-" + i + "-protocol." + fileEnding;
                } else {
                    destinationFile = destinationFolder + dtf.format(now) + "-" + i + "." + fileEnding;
                }
                current.setPictureUrl(destinationFile);
                current.setInventoryItem(item);
                Files.createDirectories(Path.of(destinationFolder));
                File f = new File(destinationFile);

                if (fileEnding.equals("pdf")) {
                    try (FileOutputStream fos = new FileOutputStream(f)) {
                        fos.write(decodedImg);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    } finally {
                        inventoryManagementService.addPicture(current);
                    }
                } else {
                    try {
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedImg));
                        ImageIO.write(img, fileEnding, f);
                        String thumbnailFolder = pathToThumbnailsFolder + item.getItemInternalNumber() + "/";
                        Files.createDirectories(Path.of(thumbnailFolder));
                        String thumbnailFile = new ThumbnailGenerator().generateThumbnail(f, thumbnailFolder);
                        current.setThumbnailUrl(thumbnailFile);
                    } catch (IOException e) {
                        log.error(e.getMessage());
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

    public List<Picture> parseBase64WithThumbnail(List<Picture> pictures) {
        return pictures.stream().map(picture -> {
            File f = new File(picture.getPictureUrl());
            String fileType = getFileType(picture.getPictureUrl());
            String imageString;
            imageString = getBaseImageString(fileType);

            if (StringUtils.isNotEmpty(picture.getThumbnailUrl())) {
                f = new File(picture.getThumbnailUrl());
            }

            return convertPicture(picture, f, imageString, true);
        }).toList();
    }

    private Picture convertPicture(Picture picture, File f, String imageString,
                                   boolean useThumbnail) {
        imageString = readFileBase64(f, imageString);
        Picture pictureFinal = new Picture();

        if (StringUtils.isNotEmpty(picture.getThumbnailUrl()) && useThumbnail) {
            pictureFinal.setThumbnailUrl(imageString);
        } else {
            pictureFinal.setPictureUrl(imageString);
        }
        pictureFinal.setInventoryItem(picture.getInventoryItem());
        pictureFinal.setId(picture.getId());
        return pictureFinal;
    }

    private static String getBaseImageString(String fileType) {
        String imageString;
        if (fileType.equals("pdf")) {
            imageString = "data:application/pdf;base64,";
        } else {
            imageString = "data:image/" + fileType + ";base64,";

        }
        return imageString;
    }

    public Picture parseBase64WithoutThumbnail(Picture picture) {

        File f = new File(picture.getPictureUrl());
        String fileType = getFileType(picture.getPictureUrl());
        String imageString;

        imageString = getBaseImageString(fileType);
        return convertPicture(picture, f, imageString, false);
    }

    private String readFileBase64(File f, String imageString) {
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imageString;
    }

}

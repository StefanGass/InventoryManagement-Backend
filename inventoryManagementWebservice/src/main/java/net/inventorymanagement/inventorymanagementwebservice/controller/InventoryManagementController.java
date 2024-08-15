package net.inventorymanagement.inventorymanagementwebservice.controller;

import net.inventorymanagement.inventorymanagementwebservice.dtos.ChartItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.dtos.DetailInventoryItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.dtos.InventoryItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.facades.InventoryItemFacade;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import net.inventorymanagement.inventorymanagementwebservice.repositories.InventoryItemRepository;
import net.inventorymanagement.inventorymanagementwebservice.service.InventoryManagementService;
import net.inventorymanagement.inventorymanagementwebservice.utils.DroppingQueueEnum;
import net.inventorymanagement.inventorymanagementwebservice.utils.ExcelFileGenerator;
import net.inventorymanagement.inventorymanagementwebservice.utils.ItemComparator;
import net.inventorymanagement.inventorymanagementwebservice.utils.PythonExecutor;
import jakarta.ws.rs.QueryParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/inventorymanagement")
@Log4j2
public class InventoryManagementController {

    @Value("${path.to.resources.folder}")
    private String pathToResourcesFolder;

    private final InventoryManagementService inventoryManagementService;
    private final InventoryItemFacade inventoryItemFacade;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryManagementController(InventoryManagementService inventoryManagementService, InventoryItemFacade inventoryItemFacade, InventoryItemRepository inventoryItemRepository) {
        this.inventoryManagementService = inventoryManagementService;
        this.inventoryItemFacade = inventoryItemFacade;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    // ####################### Inventory #######################

    @GetMapping(path = "inventory")
    public List<InventoryItemDTO> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = inventoryManagementService.getAllInventoryItems();
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    @GetMapping(path = "inventory/internal_number/{type}")
    public String getItemInternalNumberByType(@PathVariable("type") String typeName) {
        return inventoryManagementService.generateInventoryInternalNumber(inventoryManagementService.getTypeByName(typeName));
    }

    @GetMapping(path = "inventory/department/{id}")
    public List<InventoryItemDTO> getInventoryItemsByDepartmentId(
            @PathVariable("id") Integer departmentId) throws Exception {
        List<InventoryItem> inventoryItems = inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    @GetMapping("inventory/{id}")
    public DetailInventoryItemDTO getInventoryItem(@PathVariable int id) {
        InventoryItem item = inventoryManagementService.getInventoryItemById(id);
        item.getChange().sort(Comparator.reverseOrder());
        return inventoryItemFacade.mapModelToDetailDTO(item);
    }

    @GetMapping("inventory/search/{internalNumber}")
    public Integer getInventoryItemByInternalNumber(@PathVariable String internalNumber) {
        InventoryItem item = inventoryManagementService.getInventoryItemByInternalNumber(internalNumber);
        return item.getId();
    }

    @PostMapping(path = "inventory")
    public InventoryItemDTO addInventoryItem(@RequestBody DetailInventoryItemDTO model) throws Exception {
        InventoryItem item = inventoryItemFacade.mapDTOToModel(model, null);
        item = inventoryManagementService.addInventory(item);
        inventoryManagementService.addChange(getChange("Inventargegenstand angelegt.", item, model.getUserName(), item.toString(getPictureCounter(model))));
        inventoryItemFacade.savePictures(model.getPictures(), item, false);
        PythonExecutor.generateQrCodeWithPythonScript(pathToResourcesFolder, item.getId(), item.getItemInternalNumber());
        return inventoryItemFacade.mapModelToDTO(item);
    }

    @PatchMapping("inventory/{id}")
    public InventoryItem updateInventoryItem(
            @PathVariable(value = "id") int id, @RequestBody DetailInventoryItemDTO itemDetails) throws Exception {
        InventoryItem inventoryItemToUpdate = inventoryManagementService.getInventoryItemById(id);
        InventoryItem originalItem = inventoryItemToUpdate.clone();
        inventoryItemToUpdate = inventoryItemFacade.mapDTOToModel(itemDetails, inventoryItemToUpdate);
        String change_history = ItemComparator.getChangeString(originalItem, inventoryItemToUpdate, getPictureCounter(itemDetails));
        if (!change_history.isEmpty()) {
            if (Objects.equals(inventoryItemToUpdate.getPieces(), inventoryItemToUpdate.getPiecesDropped())) {
                inventoryManagementService.addChange(getChange("Inventargegenstand ausgeschieden.", inventoryItemToUpdate, itemDetails.getUserName(), change_history));
            } else {
                inventoryManagementService.addChange(getChange("Inventargegenstand bearbeitet.", inventoryItemToUpdate, itemDetails.getUserName(), change_history));
            }
        }
        if (itemDetails.getPictures() != null && !itemDetails.getPictures().isEmpty()) {
            inventoryItemFacade.savePictures(itemDetails.getPictures(), inventoryItemToUpdate, false);
        }
        return inventoryItemRepository.save(inventoryItemToUpdate);
    }

    @GetMapping(path = "inventory/droppingQueue")
    public List<InventoryItemDTO> getAllDroppingQueueInventoryItems() {
        List<InventoryItem> inventoryItems = inventoryManagementService.getAllInventoryItems();
        return inventoryItems.stream().filter(item -> item.getDroppingQueue() != null)
                .map(inventoryItemFacade::mapModelToDetailDTO)
                .collect(Collectors.toList());
    }


    @GetMapping(path = "inventory/department/{id}/droppingQueue")
    public List<InventoryItemDTO> getDroppingQueueInventoryItemsByDepartmentId(
            @PathVariable("id") Integer departmentId) throws Exception {
        List<InventoryItem> inventoryItems =
                inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
        return inventoryItems.stream().filter(item -> item.getDroppingQueue() != null)
                .map(inventoryItemFacade::mapModelToDetailDTO)
                .collect(Collectors.toList());
    }

    @PatchMapping("inventory/{id}/droppingQueue/revoke")
    public boolean revokeDroppingInventoryItem(@RequestBody InventoryItemDTO inventoryItemDTO, @PathVariable int id) {
        InventoryItem inventoryItem = inventoryManagementService.getInventoryItemById(id);
        if (inventoryItem.getDroppingQueue().equals(DroppingQueueEnum.AUSSCHEIDEN.toString())) {
            inventoryManagementService.addChange(getChange("Inventargegenstand bearbeitet.", inventoryItem, inventoryItemDTO.getUserName(), "Ausscheidung abgebrochen."));
        } else if (inventoryItem.getDroppingQueue().equals(DroppingQueueEnum.DEAKTIVIEREN.toString())) {
            inventoryManagementService.addChange(getChange("Inventargegenstand bearbeitet.", inventoryItem, inventoryItemDTO.getUserName(), "Deaktivierung abgebrochen."));
        } else {
            return false; // the item was dropped already
        }
        inventoryManagementService.revokeDroppingInventoryItem(inventoryItem);
        return true;
    }

    @PatchMapping("inventory/{id}/deactivate")
    public InventoryItem deactivateInventoryItemApprove(
            @RequestBody InventoryItemDTO inventoryItemDTO, @PathVariable int id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.deactivateInventoryItem(id);
        inventoryManagementService.addChange(getChange("Inventargegenstand deaktiviert.", inventoryItem, inventoryItemDTO.getUserName(), "Deaktivierung bestätigt."));
        return inventoryItem;
    }

    @PatchMapping("inventory/{id}/activate")
    public InventoryItem activateInventoryItem(@RequestBody InventoryItemDTO inventoryItemDTO, @PathVariable int id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.activateInventoryItem(id);
        inventoryManagementService.addChange(getChange("Inventargegenstand aktiviert.", inventoryItem, inventoryItemDTO.getUserName(), "Gegenstand mit Admin-Privilegien reaktiviert."));
        return inventoryItem;
    }

    @PatchMapping("inventory/transferprotocol/{id}/from/{firstName}/{lastName}")
    public InventoryItem addTransferProtocol(
            @PathVariable(value = "id") int id,
            @PathVariable(value = "firstName") String firstName,
            @PathVariable(value = "lastName") String lastName,
            @RequestBody Picture picture) throws Exception {
        InventoryItem inventoryItemToUpdate = inventoryManagementService.getInventoryItemById(id);
        String userName = firstName + " " + lastName;
        inventoryManagementService.addChange(getChange("Inventargegenstand ausgegeben.", inventoryItemToUpdate, userName, "Ausgabeprotokoll hinzugefügt, ausgegeben an: {" + inventoryItemToUpdate.getIssuedTo() + "}"));
        String pdfBase64 = PythonExecutor.generateTransferProtocolWithPythonScript(pathToResourcesFolder, inventoryItemToUpdate, picture, userName);
        if (pdfBase64 != null) {
            picture.setPictureUrl(pdfBase64);
            List<Picture> pictureList = new ArrayList<>();
            pictureList.add(picture);
            inventoryItemToUpdate.setPictures(pictureList);
            inventoryItemFacade.savePictures(inventoryItemToUpdate.getPictures(), inventoryItemToUpdate, true);
            return inventoryItemRepository.save(inventoryItemToUpdate);
        } else {
            throw new Exception("Übergabeprotokoll konnte nicht hinzugefügt werden.");
        }
    }

    private static int getPictureCounter(DetailInventoryItemDTO model) {
        int pictureCounter = 0;
        if (model.getPictures() != null) {
            pictureCounter = model.getPictures().size();
        }
        return pictureCounter;
    }

    private Change getChange(String changeStatus, InventoryItem savedItem, String userName, String changeHistory) {
        Change change = new Change();
        change.setChangeStatus(changeStatus);
        change.setInventoryItem(savedItem);
        change.setUser(userName);
        change.setChangeDate(LocalDateTime.now());
        if (changeHistory != null) {
            change.setChangeHistory(changeHistory);
        }
        return change;
    }

    // ####################### Search #######################

    @GetMapping(path = "inventory/search")
    public List<InventoryItemDTO> quickSearchAllDepartments(@QueryParam("search") String search) {
        List<InventoryItem> inventoryItemList = inventoryManagementService.quickSearchAllDepartments(search);
        return inventoryItemList.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    @GetMapping(path = "inventory/search/department/{id}")
    public List<InventoryItemDTO> quickSearchByDepartment(@PathVariable("id") Integer departmentId, @QueryParam("search") String search) {
        List<InventoryItem> inventoryItemList = inventoryManagementService.quickSearchByDepartment(search, departmentId);
        return inventoryItemList.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    // ####################### Export #######################

    @GetMapping("inventory/export")
    public byte[] export(@RequestParam(required = false) Integer departmentId, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Integer typeId,
                         @RequestParam(required = false) Integer locationId, @RequestParam(required = false) Integer supplierId, @RequestParam(required = false) String status,
                         @RequestParam(required = false) String creationDateFrom, @RequestParam(required = false) String creationDateTo,
                         @RequestParam(required = false) String deliveryDateFrom, @RequestParam(required = false) String deliveryDateTo,
                         @RequestParam(required = false) String issueDateFrom, @RequestParam(required = false) String issueDateTo,
                         @RequestParam(required = false) String droppingDateFrom, @RequestParam(required = false) String droppingDateTo) throws IOException {
        LocalDateTime parsedDeliveryDateFrom = null;
        LocalDateTime parsedDeliveryDateTo = null;
        LocalDateTime parsedIssueDateFrom = null;
        LocalDateTime parsedIssueDateTo = null;
        LocalDateTime parsedDroppingDateFrom = null;
        LocalDateTime parsedDroppingDateTo = null;
        LocalDateTime parsedCreationDateFrom = null;
        LocalDateTime parsedCreationDateTo = null;

        /* for testing purpose
        System.out.println("Department ID: " + departmentId +
                ", Category ID: " + categoryId +
                ", Type ID: " + typeId +
                ", Location ID: " + locationId +
                ", Supplier ID: " + supplierId +
                ", Status: " + status +
                ", Creation Date From: " + creationDateFrom +
                ", Creation Date To: " + creationDateTo +
                ", Delivery Date From: " + deliveryDateFrom +
                ", Delivery Date To: " + deliveryDateTo +
                ", Issue Date From: " + issueDateFrom +
                ", Issue Date To: " + issueDateTo +
                ", Dropping Date From: " + droppingDateFrom +
                ", Dropping Date To: " + droppingDateTo);
         */

        if (creationDateFrom != null && !Objects.equals(creationDateFrom, "")) {
            parsedCreationDateFrom = LocalDateTime.parse(creationDateFrom.replace("Z", ""));
        }
        if (creationDateTo != null && !Objects.equals(creationDateTo, "")) {
            String date = creationDateTo.replace("Z", "");
            date = date.replace("00:00:00", "23:59:59");
            parsedCreationDateTo = LocalDateTime.parse(date);
        }
        if (deliveryDateFrom != null && !Objects.equals(deliveryDateFrom, "")) {
            parsedDeliveryDateFrom = LocalDateTime.parse(deliveryDateFrom.replace("Z", ""));
        }
        if (deliveryDateTo != null && !Objects.equals(deliveryDateTo, "")) {
            parsedDeliveryDateTo = LocalDateTime.parse(deliveryDateTo.replace("Z", ""));
        }
        if (issueDateFrom != null && !Objects.equals(issueDateFrom, "")) {
            parsedIssueDateFrom = LocalDateTime.parse(issueDateFrom.replace("Z", ""));
        }
        if (issueDateTo != null && !Objects.equals(issueDateTo, "")) {
            parsedIssueDateTo = LocalDateTime.parse(issueDateTo.replace("Z", ""));
        }
        if (droppingDateFrom != null && !Objects.equals(droppingDateFrom, "")) {
            parsedDroppingDateFrom = LocalDateTime.parse(droppingDateFrom.replace("Z", ""));
        }
        if (droppingDateTo != null && !Objects.equals(droppingDateTo, "")) {
            parsedDroppingDateTo = LocalDateTime.parse(droppingDateTo.replace("Z", ""));
        }

        String filterString = inventoryManagementService.getFilterString(departmentId, categoryId, typeId, locationId, supplierId, status,
                parsedCreationDateFrom, parsedCreationDateTo, parsedDeliveryDateFrom, parsedDeliveryDateTo,
                parsedIssueDateFrom, parsedIssueDateTo, parsedDroppingDateFrom, parsedDroppingDateTo);
        List<InventoryItem> inventoryItems =
                inventoryItemRepository.findByOptionalParameters(departmentId, categoryId, typeId, locationId, supplierId, status,
                        parsedCreationDateFrom, parsedCreationDateTo, parsedDeliveryDateFrom, parsedDeliveryDateTo,
                        parsedIssueDateFrom, parsedIssueDateTo, parsedDroppingDateFrom, parsedDroppingDateTo);
        ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator();
        return excelFileGenerator.generateFile(inventoryItems, filterString);
    }

    // ####################### Item name #######################

    @GetMapping(path = "itemname/{departmentId}")
    public List<String> getAllItemNamesByDepartment(@PathVariable("departmentId") Integer departmentId) {
        return inventoryManagementService.getAllItemNamesByDepartment(departmentId);
    }

    // ####################### Type #######################

    @GetMapping(path = "type")
    public List<Type> getAllTypes() {
        return inventoryManagementService.getAllTypes();
    }

    @PostMapping(path = "type")
    public String addType(@RequestBody Type type) throws Exception {
        inventoryManagementService.addType(type);
        return "Typ \"" + type.getTypeName() + "\" erfolgreich hinzugefügt.";
    }

    // ####################### Category #######################

    @GetMapping(path = "category")
    public List<Category> getAllCategories() {
        return inventoryManagementService.getAllCategories();
    }

    @PostMapping(path = "category")
    public String addCategory(@RequestBody Category category) throws Exception {
        inventoryManagementService.addCategory(category);
        return "Kategorie \"" + category.getCategoryName() + "\" erfolgreich hinzugefügt.";
    }

    // ####################### Location #######################

    @GetMapping(path = "location")
    public List<Location> getAllLocations() {
        return inventoryManagementService.getAllLocations();
    }

    @PostMapping(path = "location")
    public String addLocation(@RequestBody Location location) throws Exception {
        inventoryManagementService.addLocation(location);
        return "Standort \"" + location.getLocationName() + "\" erfolgreich hinzugefügt.";
    }

    // ####################### Room #######################

    @GetMapping(path = "room/{departmentId}")
    public List<String> getAllRoomsByDepartment(@PathVariable("departmentId") Integer departmentId) {
        return inventoryManagementService.getAllRoomsByDepartment(departmentId);
    }

    // ####################### Supplier #######################

    @GetMapping(path = "supplier")
    public List<Supplier> getAllSuppliers() {
        return inventoryManagementService.getAllSuppliers();
    }

    @PostMapping(path = "supplier")
    public String addSupplier(@RequestBody Supplier supplier) throws Exception {
        inventoryManagementService.addSupplier(supplier);
        return "Lieferant \"" + supplier.getSupplierName() + "\" erfolgreich hinzugefügt.";
    }

    // ####################### Pictures #######################

    @GetMapping(path = "picture/{id}")
    public Picture getPicture(@PathVariable("id") Integer pictureId) {
        var picture = inventoryManagementService.getPicture(pictureId);
        if (picture == null) {
            return null;
        }
        return inventoryItemFacade.parseBase64WithoutThumbnail(picture);
    }
    // ####################### Department #######################

    @GetMapping(path = "department")
    public List<Department> getAllDepartments() {
        return inventoryManagementService.getAllDepartments();
    }

    @GetMapping(path = "department/user/{id}")
    public Department getDepartmentByUserId(@PathVariable("id") Integer userId) throws Exception {
        return inventoryManagementService.getDepartmentByUserId(userId);
    }

    @PostMapping(path = "department")
    public String addDepartment(@RequestBody Department department) throws Exception {
        inventoryManagementService.addDepartment(department);
        return "Abteilung \"" + department.getDepartmentName() + "\" erfolgreich hinzugefügt.";
    }

    // ####################### Department members #######################

    @GetMapping(path = "department/{id}")
    public List<DepartmentMember> getAllDepartmentMembersByDepartmentId(@PathVariable("id") Integer departmentId) {
        return inventoryManagementService.getAllDepartmentMembersFromDepartmentId(departmentId);
    }

    @GetMapping(path = "department/member/{id}")
    public DepartmentMember getDepartmentMember(@PathVariable("id") Integer userId) {
        return inventoryManagementService.getDepartmentMemberByUserId(userId);
    }

    @PostMapping(path = "department/member/{id}")
    public String addDepartmentMemberToDepartment(@PathVariable("id") Integer departmentId,
                                                  @RequestBody Integer userId) throws Exception {
        Department department = inventoryManagementService.addDepartmentMemberToDepartment(departmentId, userId);
        return "User erfolgreich zur Abteilung \"" + department.getDepartmentName() + "\" hinzugefügt.";
    }

    @PatchMapping(path = "department/member/{id}/reviewer")
    public void updateReviewer(@PathVariable("id") Integer memberId,
                               @RequestBody boolean reviewer) {
        var member = inventoryManagementService.getDepartmentMemberByUserId(memberId);
        member.setDroppingReviewer(reviewer);
        inventoryManagementService.updateDepartmentMember(member);
    }

    @DeleteMapping(path = "department/member/{id}")
    public String removeDepartmentMemberFromDepartment(@PathVariable("id") Integer departmentId,
                                                       @RequestBody Integer userId) throws Exception {
        Department department = inventoryManagementService.removeDepartmentMemberFromDepartment(departmentId, userId);
        return "User erfolgreich von der Abteilung \"" + department.getDepartmentName() + "\" entfernt.";
    }

    // ####################### Print #######################

    @GetMapping("printer")
    public List<Printer> getAllPrinters() {
        return inventoryManagementService.getAllPrinters();
    }

    @GetMapping("printer/{id}")
    public List<Printer> getAllPrinters(@PathVariable(value = "id") Integer userId) {
        return inventoryManagementService.getAllPrintersAndSortByUserId(userId);
    }

    @PostMapping(path = "printer")
    public String addPrinter(@RequestBody Printer printer) throws Exception {
        inventoryManagementService.addPrinter(printer);
        return "Drucker \"" + printer.getPrinterName() + "\" mit IP \"" + printer.getPrinterIp() + "\" erfolgreich hinzugefügt.";
    }

    @GetMapping("printer/{id}/print/{itemId}/pieces/{pieces}/user/{userId}")
    public String printQrCode(
            @PathVariable(value = "id") Integer printerId,
            @PathVariable(value = "itemId") Integer itemId,
            @PathVariable(value = "pieces") Integer pieces,
            @PathVariable(value = "userId") Integer userId
    ) throws Exception {
        inventoryManagementService.printQrCodeAndSetDefaultPrinter(printerId, itemId, pieces, userId);
        return pieces + " Etikett(en) von Item ID \"" + itemId + "\" auf Drucker ID \"" + printerId + "\" von User \"" + userId + "\" gedruckt.";
    }

    // ####################### Charts #######################

    @GetMapping(path = "chart/activity")
    public List<ChartItemDTO> getActivityChart() throws Exception {
        return inventoryManagementService.getActivityChartItems();
    }

    @GetMapping(path = "chart/activity/department/{id}")
    public List<ChartItemDTO> getActivityChart(@PathVariable("id") Integer departmentId)
            throws Exception {
        return inventoryManagementService.getActivityChartItemsByDepartment(departmentId);
    }

    @GetMapping(path = "chart/last_items")
    public List<InventoryItemDTO> getLastTwentyItems(
            @QueryParam("search") Optional<String> search) {
        List<InventoryItem> inventoryItemList;
        if (search.isPresent() && !search.get().isBlank()) {
            return quickSearchAllDepartments(search.get());
        } else {
            inventoryItemList = inventoryManagementService.getAllInventoryItems();
            List<InventoryItemDTO> inventoryItemsDTO = new ArrayList<>(inventoryItemList.stream().map(inventoryItemFacade::mapModelToDTO).toList());
            inventoryItemsDTO.sort(Comparator.reverseOrder());
            if (inventoryItemsDTO.size() > 20) {
                return inventoryItemsDTO.subList(0, 20);
            } else {
                return inventoryItemsDTO;
            }
        }
    }

    @GetMapping(path = "chart/last_items/department/{id}")
    public List<InventoryItemDTO> getLastTenItemsByDepartmentId(
            @PathVariable("id") Integer departmentId, @QueryParam("search") Optional<String> search) throws Exception {
        List<InventoryItem> inventoryItemList;
        if (search.isPresent() && !search.get().isBlank()) {
            return quickSearchByDepartment(departmentId, search.get());
        } else {
            inventoryItemList = inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
            List<InventoryItemDTO> inventoryItemsDTO = new ArrayList<>(inventoryItemList.stream().map(inventoryItemFacade::mapModelToDTO).toList());
            inventoryItemsDTO.sort(Comparator.reverseOrder());
            if (inventoryItemsDTO.size() > 10) {
                return inventoryItemsDTO.subList(0, 10);
            } else {
                return inventoryItemsDTO;
            }
        }
    }

    @GetMapping(path = "chart/type")
    public List<ChartItemDTO> getTypeChartByDepartment() {
        return inventoryManagementService.getTypeChartItems();
    }

    @GetMapping(path = "chart/type/department/{id}")
    public List<ChartItemDTO> getTypeChartByDepartment(@PathVariable("id") Integer departmentId) {
        return inventoryManagementService.getTypeChartItemsByDepartment(departmentId);
    }

    @GetMapping(path = "chart/department")
    public List<ChartItemDTO> getDepartmentChart() {
        return inventoryManagementService.getDepartmentItemChart();
    }

}

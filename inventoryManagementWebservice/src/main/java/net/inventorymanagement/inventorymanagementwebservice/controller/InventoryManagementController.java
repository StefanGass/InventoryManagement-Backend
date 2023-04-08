package net.inventorymanagement.inventorymanagementwebservice.controller;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.ws.rs.*;
import net.inventorymanagement.inventorymanagementwebservice.dtos.*;
import net.inventorymanagement.inventorymanagementwebservice.facades.*;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import net.inventorymanagement.inventorymanagementwebservice.repositories.*;
import net.inventorymanagement.inventorymanagementwebservice.service.*;
import net.inventorymanagement.inventorymanagementwebservice.utils.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cloud.netflix.eureka.*;
import org.springframework.web.bind.annotation.*;

@EnableEurekaClient
@RestController
@RequestMapping("api/inventorymanagement")
public class InventoryManagementController {

    @Autowired
    private InventoryManagementService inventoryManagementService;

    @Autowired
    private InventoryItemFacade inventoryItemFacade;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private SmartSearchService smartSearchService;

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    // ####################### Inventory #######################

    @GetMapping(path = "inventory")
    public List<InventoryItemDTO> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = inventoryManagementService.getAllInventoryItems();
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO)
            .collect(Collectors.toList());
    }

    @GetMapping(path = "inventory/internal_number/{type}")
    public String getItemInternalNumberByType(@PathVariable("type") String typeName) {
        return inventoryManagementService.generateInventoryInternalNumber(
            inventoryManagementService.getTypeByName(typeName));
    }

    @GetMapping(path = "inventory/department/{id}")
    public List<InventoryItemDTO> getInventoryItemsByDepartmentId(
        @PathVariable("id") Integer departmentId) throws Exception {
        List<InventoryItem> inventoryItems =
            inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("inventory/{id}")
    public DetailInventoryItemDTO getInventoryItem(@PathVariable String id) {
        InventoryItem item = inventoryManagementService.getInventoryItemById(id);
        item.getChange().sort(Comparator.reverseOrder());
        return inventoryItemFacade.mapModelToDetailDTO(item);
    }

    @GetMapping("inventory/search/{internalNumber}")
    public Integer getInventoryItemByInternalNumber(@PathVariable String internalNumber) {
        InventoryItem item =
            inventoryManagementService.getInventoryItemByInternalNumber(internalNumber);
        return item.getId();
    }

    @PostMapping(path = "inventory")
    public InventoryItemDTO addInventoryItem(@RequestBody DetailInventoryItemDTO model)
        throws Exception {
        InventoryItem item = inventoryItemFacade.mapDTOToModel(model, null);
        item = inventoryManagementService.addInventory(item);
        inventoryManagementService.addChange(
            getChange("Inventargegenstand angelegt.", item, model.getUserName(),
                item.toString(getPictureCounter(model))));
        inventoryItemFacade.savePictures(model.getPictures(), item, false);
        PythonExecutor.generateQrCodeWithPythonScript(item.getId(), item.getItemInternalNumber());
        return inventoryItemFacade.mapModelToDTO(item);
    }

    @PatchMapping("inventory/{id}")
    public InventoryItem updateInventoryItem(
        @PathVariable(value = "id") String id,
        @RequestBody DetailInventoryItemDTO itemDetails) throws Exception {
        InventoryItem inventoryItemToUpdate = inventoryManagementService.getInventoryItemById(id);
        InventoryItem originalItem = inventoryItemToUpdate.clone();
        inventoryItemToUpdate =
            inventoryItemFacade.mapDTOToModel(itemDetails, inventoryItemToUpdate);
        String change_history = ItemComparator.getChangeString(originalItem, inventoryItemToUpdate,
            getPictureCounter(itemDetails));
        if (!change_history.equals("")) {
            if (Objects.equals(inventoryItemToUpdate.getPieces(),
                inventoryItemToUpdate.getPiecesDropped())) {
                inventoryManagementService.addChange(
                    getChange("Inventargegenstand ausgeschieden.", inventoryItemToUpdate,
                        itemDetails.getUserName(), change_history));
            } else {
                inventoryManagementService.addChange(
                    getChange("Inventargegenstand bearbeitet.", inventoryItemToUpdate,
                        itemDetails.getUserName(), change_history));
            }
        }
        if (itemDetails.getPictures() != null && itemDetails.getPictures().size() > 0) {
            System.out.println(itemDetails.getPictures().get(0).toString());
            inventoryItemFacade.savePictures(itemDetails.getPictures(), inventoryItemToUpdate,
                false);
        }
        return inventoryItemRepository.save(inventoryItemToUpdate);
    }

    @PatchMapping("inventory/{id}/deactivate")
    public InventoryItem deactivateInventoryItem(
        @RequestBody InventoryItemDTO inventoryItemDTO,
        @PathVariable String id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.deactivateInventoryItem(id);
        inventoryManagementService.addChange(
            getChange("Inventargegenstand deaktiviert.", inventoryItem,
                inventoryItemDTO.getUserName(), null));
        return inventoryItem;
    }

    @PatchMapping("inventory/{id}/activate")
    public InventoryItem activateInventoryItem(@RequestBody InventoryItemDTO inventoryItemDTO,
                                               @PathVariable String id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.activateInventoryItem(id);
        inventoryManagementService.addChange(
            getChange("Inventargegenstand aktiviert.", inventoryItem,
                inventoryItemDTO.getUserName(), null));
        return inventoryItem;
    }

    @PatchMapping("inventory/transferprotocol/{id}/from/{firstName}/{lastName}")
    public InventoryItem addTransferProtocol(
        @PathVariable(value = "id") String id,
        @PathVariable(value = "firstName") String firstName,
        @PathVariable(value = "lastName") String lastName,
        @RequestBody Picture picture) throws Exception {
        InventoryItem inventoryItemToUpdate = inventoryManagementService.getInventoryItemById(id);
        String userName = firstName + " " + lastName;
        inventoryManagementService.addChange(
            getChange("Inventargegenstand ausgegeben.", inventoryItemToUpdate, userName,
                "Ausgabeprotokoll hinzugefügt, ausgegeben an: {" +
                    inventoryItemToUpdate.getIssuedTo() + "}"));
        String pdfBase64 =
            PythonExecutor.generateTransferProtocolWithPythonScript(inventoryItemToUpdate, picture,
                userName);
        if (pdfBase64 != null) {
            picture.setPictureUrl(pdfBase64);
            List<Picture> pictureList = new ArrayList<>();
            pictureList.add(picture);
            inventoryItemToUpdate.setPictures(pictureList);
            inventoryItemFacade.savePictures(inventoryItemToUpdate.getPictures(),
                inventoryItemToUpdate, true);
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

    private Change getChange(String changeStatus, InventoryItem savedItem, String userName,
                             String changeHistory) {
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
    public List<DepartmentMember> getAllDepartmentMembersByDepartmentId(
        @PathVariable("id") Integer departmentId) {
        return inventoryManagementService.getAllDepartmentMembersFromDepartmentId(departmentId);
    }

    @PostMapping(path = "department/member/{id}")
    public String addDepartmentMemberToDepartment(@PathVariable("id") Integer departmentId,
                                                  @RequestBody Integer userId) throws Exception {
        Department department =
            inventoryManagementService.addDepartmentMemberToDepartment(departmentId, userId);
        return "User erfolgreich zur Abteilung \"" + department.getDepartmentName() +
            "\" hinzugefügt.";
    }

    @DeleteMapping(path = "department/member/{id}")
    public String removeDepartmentMemberFromDepartment(@PathVariable("id") Integer departmentId,
                                                       @RequestBody Integer userId)
        throws Exception {
        Department department =
            inventoryManagementService.removeDepartmentMemberFromDepartment(departmentId, userId);
        return "User erfolgreich von der Abteilung \"" + department.getDepartmentName() +
            "\" entfernt.";
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
        return "Drucker \"" + printer.getPrinterName() + "\" mit IP \"" + printer.getPrinterIp() +
            "\" erfolgreich hinzugefügt.";
    }

    @GetMapping("printer/{id}/print/{itemId}/pieces/{pieces}/user/{userId}")
    public String printQrCode(
        @PathVariable(value = "id") Integer printerId,
        @PathVariable(value = "itemId") Integer itemId,
        @PathVariable(value = "pieces") Integer pieces,
        @PathVariable(value = "userId") Integer userId
    ) throws Exception {
        inventoryManagementService.printQrCodeAndSetDefaultPrinter(printerId, itemId, pieces,
            userId);
        return pieces + " Etikett(en) von Item ID \"" + itemId + "\" auf Drucker ID \"" +
            printerId + "\" von User \"" + userId + "\" gedruckt.";
    }

    // ####################### Charts #######################

    @GetMapping(path = "chart/activity/")
    public List<ChartItemDTO> getActivityChart() throws Exception {
        return inventoryManagementService.getActivityChartItems();
    }

    @GetMapping(path = "chart/activity/department/{id}")
    public List<ChartItemDTO> getActivityChart(@PathVariable("id") Integer departmentId)
        throws Exception {
        return inventoryManagementService.getActivityChartItemsByDepartment(departmentId);
    }

    @GetMapping(path = "chart/last_items/")
    public List<InventoryItemDTO> getLastTwentyItems(
        @QueryParam("search") Optional<String> search) {
        List<InventoryItem> inventoryItems;
        if (search.isPresent() && !search.get().isBlank()) {
            inventoryItems = smartSearchService.getPostBasedOnWord(null, search.get());
        } else {
            inventoryItems = inventoryManagementService.getAllInventoryItems();
        }
        List<InventoryItemDTO> inventoryItemsDTO = new ArrayList<>(
            inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).toList());
        inventoryItemsDTO.sort(Comparator.reverseOrder());
        if (inventoryItemsDTO.size() > 20) {
            return inventoryItemsDTO.subList(0, 20);
        } else {
            return inventoryItemsDTO;
        }
    }

    @GetMapping(path = "chart/last_items/department/{id}")
    public List<InventoryItemDTO> getLastTenItemsByDepartmentId(
        @PathVariable("id") Integer departmentId, @QueryParam("search")
    Optional<String> search) throws Exception {
        List<InventoryItem> inventoryItems;
        if (search.isPresent() && !search.get().isBlank()) {
            inventoryItems = smartSearchService.getPostBasedOnWord(departmentId, search.get());
        } else {
            inventoryItems =
                inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
        }
        List<InventoryItemDTO> inventoryItemsDTO = new ArrayList<>(
            inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).toList());
        inventoryItemsDTO.sort(Comparator.reverseOrder());
        if (inventoryItemsDTO.size() > 10) {
            return inventoryItemsDTO.subList(0, 10);
        } else {
            return inventoryItemsDTO;
        }
    }

    @GetMapping(path = "chart/type/")
    public List<ChartItemDTO> getTypeChartByDepartment() {
        return inventoryManagementService.getTypeChartItems();
    }

    @GetMapping(path = "chart/type/department/{id}")
    public List<ChartItemDTO> getTypeChartByDepartment(@PathVariable("id") Integer departmentId) {
        return inventoryManagementService.getTypeChartItemsByDepartment(departmentId);
    }

    @GetMapping(path = "chart/department/")
    public List<ChartItemDTO> getDepartmentChart() {
        return inventoryManagementService.getDepartmentItemChart();
    }

    // ####################### Search #######################
    @PostMapping(path = "inventory/search/reindex")
    public void reindexSearch() throws InterruptedException {
        smartSearchService.reindex();
    }

    @GetMapping(path = "inventory/search")
    public List<InventoryItemDTO> smartSearch(@QueryParam("search") String search) {
        var inventoryItems = smartSearchService.getPostBasedOnWord(null, search);
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO)
            .collect(Collectors.toList());
    }

    @GetMapping(path = "inventory/search/department/{id}")
    public List<InventoryItemDTO> smartSearchByDepartment(@PathVariable("id") Integer departmentId,
                                                          @QueryParam("search") String search) {
        var inventoryItems = smartSearchService.getPostBasedOnWord(departmentId, search);

        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO)
            .collect(Collectors.toList());
    }
}

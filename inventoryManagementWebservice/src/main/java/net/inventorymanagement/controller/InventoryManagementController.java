package net.inventorymanagement.controller;

import net.inventorymanagement.dtos.DetailInventoryItemDTO;
import net.inventorymanagement.dtos.InventoryItemDTO;
import net.inventorymanagement.facades.InventoryItemFacade;
import net.inventorymanagement.model.*;
import net.inventorymanagement.repositories.InventoryItemRepository;
import net.inventorymanagement.service.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public InventoryManagementController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    // ####################### Inventory #######################

    @GetMapping(path = "inventory")
    public List<InventoryItemDTO> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = inventoryManagementService.getAllInventoryItems();
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    @GetMapping(path = "inventory/department/{id}")
    public List<InventoryItemDTO> getInventoryItemsByDepartmentId(@PathVariable("id") Integer departmentId) throws Exception {
        List<InventoryItem> inventoryItems = inventoryManagementService.getInventoryItemsByDepartmentId(departmentId);
        return inventoryItems.stream().map(inventoryItemFacade::mapModelToDTO).collect(Collectors.toList());
    }

    @PostMapping(path = "inventory")
    public String addInventoryItem(@RequestBody DetailInventoryItemDTO model) throws Exception {
        InventoryItem item = inventoryItemFacade.mapDTOToModel(model, null);
        InventoryItem savedItem = inventoryManagementService.addInventory(item);
        inventoryManagementService.addChange(getChange("Item added.", savedItem, model.getUserName()));
        inventoryItemFacade.savePictures(model.getPictures(), savedItem);
        return "Item \"" + savedItem.getItemInternalNumber() + "\" successfully added.";
    }

    @GetMapping("inventory/{id}")
    public DetailInventoryItemDTO getInventoryItem(@PathVariable String id) {
        InventoryItem item = inventoryManagementService.getInventoryItemById(id);
        return inventoryItemFacade.mapModelToDetailDTO(item);
    }

    @PatchMapping("inventory/{id}")
    public InventoryItem updateInventoryItem(
            @PathVariable(value = "id") String id,
            @RequestBody DetailInventoryItemDTO itemDetails) throws Exception {
        InventoryItem inventoryItemToUpdate = inventoryManagementService.getInventoryItemById(id);
        InventoryItem updatedItem = inventoryItemFacade.mapDTOToModel(itemDetails, inventoryItemToUpdate);
        inventoryManagementService.addChange(getChange("Item changed.", updatedItem, itemDetails.getUserName()));
        if (itemDetails.getPictures() != null && itemDetails.getPictures().size() > 0) {
            inventoryItemFacade.savePictures(itemDetails.getPictures(), updatedItem);
        }
        return inventoryItemRepository.save(updatedItem);
    }

    @PatchMapping("inventory/{id}/deactivate")
    public InventoryItem deactivateInventoryItem(
            @RequestBody InventoryItemDTO inventoryItemDTO,
            @PathVariable String id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.deactivateInventoryItem(id);
        inventoryManagementService.addChange(getChange("Item deactivated.", inventoryItem, inventoryItemDTO.getUserName()));
        return inventoryItem;
    }

    @PatchMapping("inventory/{id}/activate")
    public InventoryItem activateInventoryItem(@RequestBody InventoryItemDTO inventoryItemDTO,
                                               @PathVariable String id) throws Exception {
        InventoryItem inventoryItem = inventoryManagementService.activateInventoryItem(id);
        inventoryManagementService.addChange(getChange("Item activated.", inventoryItem, inventoryItemDTO.getUserName()));
        return inventoryItem;
    }

    private Change getChange(String changeStatus, InventoryItem savedItem, String userName) {
        Change change = new Change();
        change.setChangeStatus(changeStatus);
        change.setInventoryItem(savedItem);
        change.setUser(userName);
        change.setChangeDate(LocalDateTime.now());
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
        return "Type \"" + type.getTypeName() + "\" successfully added.";
    }

    // ####################### Category #######################

    @GetMapping(path = "category")
    public List<Category> getAllCategories() {
        return inventoryManagementService.getAllCategories();
    }

    @PostMapping(path = "category")
    public String addCategory(@RequestBody Category category) throws Exception {
        inventoryManagementService.addCategory(category);
        return "Category \"" + category.getCategoryName() + "\" successfully added.";
    }

    // ####################### Location #######################

    @GetMapping(path = "location")
    public List<Location> getAllLocations() {
        return inventoryManagementService.getAllLocations();
    }

    @PostMapping(path = "location")
    public String addLocation(@RequestBody Location location) throws Exception {
        inventoryManagementService.addLocation(location);
        return "Location \"" + location.getLocationName() + "\" successfully added.";
    }

    // ####################### Supplier #######################

    @GetMapping(path = "supplier")
    public List<Supplier> getAllSuppliers() {
        return inventoryManagementService.getAllSuppliers();
    }

    @PostMapping(path = "supplier")
    public String addSupplier(@RequestBody Supplier supplier) throws Exception {
        inventoryManagementService.addSupplier(supplier);
        return "Supplier \"" + supplier.getSupplierName() + "\" successfully added.";
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
        return "Department \"" + department.getDepartmentName() + "\" successfully added.";
    }

    // ####################### Department members #######################

    @GetMapping(path = "department/{id}")
    public List<DepartmentMember> getAllDepartmentMembersByDepartmentId(@PathVariable("id") Integer departmentId) {
        return inventoryManagementService.getAllDepartmentMembersFromDepartmentId(departmentId);
    }

    @PostMapping(path = "department/member/{id}")
    public String addDepartmentMemberToDepartment(@PathVariable("id") Integer departmentId,
                                                  @RequestBody Integer userId) throws Exception {
        Department department = inventoryManagementService.addDepartmentMemberToDepartment(departmentId, userId);
        return "Successfully assigned department \"" + department.getDepartmentName() + "\" to user \"" + userId + "\".";
    }

    @DeleteMapping(path = "department/member/{id}")
    public String removeDepartmentMemberFromDepartment(@PathVariable("id") Integer departmentId,
                                                       @RequestBody Integer userId) throws Exception {
        Department department = inventoryManagementService.removeDepartmentMemberFromDepartment(departmentId, userId);
        return "Successfully removed department \"" + department.getDepartmentName() + "\" from user \"" + userId + "\".";
    }


}

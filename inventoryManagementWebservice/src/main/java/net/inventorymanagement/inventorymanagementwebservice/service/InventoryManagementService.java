package net.inventorymanagement.inventorymanagementwebservice.service;

import net.inventorymanagement.inventorymanagementwebservice.dtos.ChartItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import net.inventorymanagement.inventorymanagementwebservice.repositories.*;
import net.inventorymanagement.inventorymanagementwebservice.utils.PythonExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InventoryManagementService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentMemberRepository departmentMemberRepository;

    @Autowired
    private PrinterRepository printerRepository;

    @Autowired
    private ChangeRepository changeRepository;

    // ####################### Inventory #######################

    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> itemList = inventoryItemRepository.findAll();
        itemList.sort(Comparator.naturalOrder());
        return itemList;
    }

    public InventoryItem getInventoryItemById(String id) {
        return inventoryItemRepository.findByItemId(Integer.valueOf(id));
    }

    public InventoryItem getInventoryItemByInternalNumber(String internalNumber) {
        return inventoryItemRepository.findByItemInternalNumber(internalNumber);
    }

    public List<InventoryItem> getInventoryItemsByDepartmentId(Integer departmentId) throws Exception {
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if (department != null) {
            List<InventoryItem> itemList = inventoryItemRepository.findByDepartmentId(departmentId);
            itemList.sort(Comparator.naturalOrder());
            return itemList;
        } else {
            throw new Exception("Abteilung \"" + departmentId + "\" existiert nicht! Bitte die IT kontaktieren!");
        }
    }

    public InventoryItem addInventory(InventoryItem model) throws Exception {
        model.setItemInternalNumber(generateInventoryInternalNumber(model.getType()));
        return inventoryItemRepository.save(checkIfInventoryItemAlreadyExists(model));
    }

    public InventoryItem deactivateInventoryItem(String id) throws Exception {
        InventoryItem inventoryItemToDeactivate = getInventoryItemById(id);
        if (inventoryItemToDeactivate.isActive()) {
            inventoryItemToDeactivate.setActive(false);
        } else {
            throw new Exception("Der Inventargegenstand \"" + inventoryItemToDeactivate.getItemInternalNumber() + "\" ist bereits inaktiv gesetzt.");
        }
        return inventoryItemRepository.save(inventoryItemToDeactivate);
    }


    public InventoryItem activateInventoryItem(String id) throws Exception {
        InventoryItem inventoryItemToActivate = getInventoryItemById(id);
        if (!inventoryItemToActivate.isActive()) {
            inventoryItemToActivate.setActive(true);
        } else {
            throw new Exception("Der Inventargegenstand \"" + inventoryItemToActivate.getItemInternalNumber() + "\" ist bereits aktiv gesetzt.");
        }
        return inventoryItemRepository.save(inventoryItemToActivate);
    }

    private InventoryItem checkIfInventoryItemAlreadyExists(InventoryItem item) throws Exception {
        InventoryItem itemDuplicate = inventoryItemRepository.findByItemInternalNumber(item.getItemInternalNumber());
        if (itemDuplicate != null) {
            throw new Exception("Inventarnummer \"" + itemDuplicate.getItemInternalNumber() + "\" ist bereits vergeben!");
        }
        return item;
    }

    public String generateInventoryInternalNumber(Type type) {
        String prefix = type.getCategory().getPrefix();
        int year = LocalDate.now().getYear();
        int counter = 0;
        List<InventoryItem> allItemList = inventoryItemRepository.findAll();
        for (InventoryItem entry : allItemList) {
            if (entry.getType().getCategory().getCategoryName().equalsIgnoreCase(type.getCategory().getCategoryName())) {
                if (Integer.parseInt(entry.getItemInternalNumber().split("-")[1]) == year) {
                    int entryNumber = Integer.parseInt(entry.getItemInternalNumber().split("-")[2]);
                    if (counter < entryNumber) {
                        counter = entryNumber;
                    }
                }
            }
        }
        counter++;
        String counterString;
        if (counter < 10) {
            counterString = "000" + counter;
        } else if (counter < 100) {
            counterString = "00" + counter;
        } else if (counter < 1000) {
            counterString = "0" + counter;
        } else {
            counterString = Integer.toString(counter);
        }
        return prefix + "-" + year + "-" + counterString;
    }

    public void addPicture(Picture model) {
        pictureRepository.save(model);
    }

    public Location getLocationByName(String locationName) {
        return locationRepository.findByLocationName(locationName);
    }

    public Supplier getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierName(supplierName);
    }

    public Type getTypeByName(String typeName) {
        return typeRepository.findByTypeName(typeName);
    }

    public Department getDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName);
    }

    // ####################### Type #######################

    public List<Type> getAllTypes() {
        List<Type> typeList = typeRepository.findAll();
        typeList.sort(Comparator.naturalOrder());
        return typeList;
    }

    public void addType(Type type) throws Exception {
        mergeCategoryWithTypeCategory(type);
        if (type.getCategory() != null) {
            typeRepository.save(Objects.requireNonNull(checkIfTypeAlreadyExists(type)));
        }
    }

    private void mergeCategoryWithTypeCategory(Type type) throws Exception {
        Category categoryOfType = type.getCategory();
        for (Category entry : categoryRepository.findAll()) {
            if (entry.getCategoryName().equalsIgnoreCase(categoryOfType.getCategoryName())) {
                type.setCategory(entry);
                return;
            }
        }
        throw new Exception("Kategorie wurde nicht gefunden! Bitte die IT kontaktieren!");
    }

    private Type checkIfTypeAlreadyExists(Type type) throws Exception {
        Type typeDuplicate = typeRepository.findByTypeName(type.getTypeName());
        if (typeDuplicate != null) {
            throw new Exception("Typ \"" + typeDuplicate.getTypeName() + "\" existiert bereits!");
        }
        return type;
    }

    // ####################### Category #######################

    public List<Category> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.sort(Comparator.naturalOrder());
        return categoryList;
    }

    public void addCategory(Category category) throws Exception {
        category.setPrefix(category.getPrefix().toUpperCase());
        categoryRepository.save(Objects.requireNonNull(checkICategoryAlreadyExists(category)));
    }

    private Category checkICategoryAlreadyExists(Category category) throws Exception {
        Category categoryDuplicateOne = categoryRepository.findByCategoryName(category.getCategoryName());
        Category categoryDuplicateTwo = categoryRepository.findByCategoryPrefix(category.getPrefix().toUpperCase());
        if (categoryDuplicateOne != null) {
            throw new Exception("Kategorie \"" + categoryDuplicateOne.getCategoryName() + "\"existiert bereits!");
        } else if (categoryDuplicateTwo != null) {
            throw new Exception("Kategorie \"" + categoryDuplicateTwo.getCategoryName() + "\"existiert bereits!");
        }
        return category;
    }

    // ####################### Location #######################

    public List<Location> getAllLocations() {
        List<Location> locationList = locationRepository.findAll();
        locationList.sort(Comparator.naturalOrder());
        return locationList;
    }

    public void addLocation(Location location) throws Exception {
        locationRepository.save(Objects.requireNonNull(checkIfLocationAlreadyExists(location)));
    }

    private Location checkIfLocationAlreadyExists(Location location) throws Exception {
        Location locationDuplicate = locationRepository.findByLocationName(location.getLocationName());
        if (locationDuplicate != null) {
            throw new Exception("Standort \"" + locationDuplicate.getLocationName() + "\" existiert bereits!");
        }
        return location;
    }

    // ####################### Supplier #######################

    public List<Supplier> getAllSuppliers() {
        List<Supplier> supplierList = supplierRepository.findAll();
        supplierList.sort(Comparator.naturalOrder());
        return supplierList;
    }

    public void addSupplier(Supplier supplier) throws Exception {
        supplierRepository.save(Objects.requireNonNull(checkIfSupplierAlreadyExists(supplier)));
    }

    private Supplier checkIfSupplierAlreadyExists(Supplier supplier) throws Exception {
        Supplier supplierDuplicate = supplierRepository.findBySupplierName(supplier.getSupplierName());
        if (supplierDuplicate != null) {
            throw new Exception("Lieferant \"" + supplierDuplicate.getSupplierName() + "\" existiert bereits!");
        }
        return supplier;
    }

    // ####################### Department #######################

    public List<Department> getAllDepartments() {
        List<Department> departmentList = departmentRepository.findAll();
        departmentList.sort(Comparator.naturalOrder());
        return departmentList;
    }

    public Department getDepartmentByUserId(Integer userId) throws Exception {
        DepartmentMember departmentMember = departmentMemberRepository.findByUserId(userId);
        if (departmentMember != null) {
            Department department = departmentMember.getDepartment();
            if (department != null) {
                return department;
            } else {
                throw new Exception("Benutzer \"" + userId + "\" ist keiner Abteilung zugeordnet! Bitte die IT kontaktieren!");
            }
        } else {
            throw new Exception("Benutzer \"" + userId + "\" ist keiner Abteilung zugeordnet oder existiert nicht! Bitte die IT kontaktieren!");
        }
    }

    public void addDepartment(Department department) throws Exception {
        departmentRepository.save(Objects.requireNonNull(checkIfDepartmentAlreadyExists(department)));
    }

    private Department checkIfDepartmentAlreadyExists(Department department) throws Exception {
        Department departmentDuplicate = departmentRepository.findByDepartmentName(department.getDepartmentName());
        if (departmentDuplicate != null) {
            throw new Exception("Abteilung \"" + departmentDuplicate.getDepartmentName() + "\" existiert bereits!");
        }
        return department;
    }

    // ####################### Department member #######################

    public List<DepartmentMember> getAllDepartmentMembersFromDepartmentId(Integer id) {
        return departmentRepository.findByDepartmentId(id).getDepartmentMembers();
    }

    public Department addDepartmentMemberToDepartment(Integer departmentId, Integer userId) throws Exception {
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if (department != null) {
            departmentMemberRepository.save(Objects.requireNonNull(checkIfDepartmentMemberIsAlreadyAssignedToADepartment(userId, department)));
            return department;
        } else {
            throw new Exception("Die Abteilung existiert nicht! Bitte die IT kontaktieren!");
        }
    }

    private DepartmentMember checkIfDepartmentMemberIsAlreadyAssignedToADepartment(Integer userId, Department department) throws Exception {
        DepartmentMember departmentMemberDuplicate = departmentMemberRepository.findByUserId(userId);
        if (departmentMemberDuplicate != null) {
            throw new Exception("User \"" + departmentMemberDuplicate.getUserId() + "\" ist bereits einer Abteilung zugeordnet! User können immer nur einer Abteilung gleichzeitig zugeordnet sein!");
        }
        return new DepartmentMember(userId, department);
    }

    public Department removeDepartmentMemberFromDepartment(Integer departmentId, Integer userId) throws Exception {
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if (department != null) {
            DepartmentMember departmentMember = departmentMemberRepository.findByUserId(userId);
            if (departmentMember != null) {
                departmentMemberRepository.delete(departmentMember);
                return department;
            } else {
                throw new Exception("Der User \"" + userId + "\" existiert nicht! Bitte die IT kontaktieren!");
            }
        } else {
            throw new Exception("Die Abteilung \"" + departmentId + "\" existiert nicht! Bitte die IT kontaktieren!");
        }
    }

    // ####################### Change #######################

    public void addChange(Change change) {
        changeRepository.save(change);
    }

    // ####################### Print #######################

    public List<Printer> getAllPrinters() {
        List<Printer> printerList = printerRepository.findAll();
        printerList.sort(Comparator.naturalOrder());
        return printerList;
    }

    public List<Printer> getAllPrintersAndSortByUserId(Integer userId) {
        List<Printer> printerList = printerRepository.findAll();
        printerList.sort(Comparator.naturalOrder());
        if (departmentMemberRepository.findByUserId(userId) != null) {
            Printer preferredPrinter = departmentMemberRepository.findByUserId(userId).getPrinter();
            if (preferredPrinter != null && printerList.indexOf(preferredPrinter) != 0) {
                printerList.remove(preferredPrinter);
                printerList.add(0, preferredPrinter);
            }
        }
        return printerList;
    }

    public void addPrinter(Printer printer) throws Exception {
        printer.setPrinterIp("tcp://" + printer.getPrinterIp());
        printer.setPrinterModel("QL-820NWB");
        printer.setLabelFormat("17x54");
        printerRepository.save(Objects.requireNonNull(checkIfPrinterAlreadyExists(printer)));
    }

    private Printer checkIfPrinterAlreadyExists(Printer printer) throws Exception {
        Printer printerDuplicateOne = printerRepository.findByPrinterName(printer.getPrinterName());
        Printer printerDuplicateTwo = printerRepository.findByPrinterIp(printer.getPrinterIp());
        if (printerDuplicateOne != null && printerDuplicateTwo != null && (!printer.getPrinterName().equalsIgnoreCase(printerDuplicateOne.getPrinterName()) || !printer.getPrinterIp().equalsIgnoreCase(printerDuplicateTwo.getPrinterIp()))) {
            throw new Exception("Drucker \"" + printer.getPrinterName() + "\", + IP \"" + printer.getPrinterIp() + "\" existiert bereits!");
        }
        return printer;
    }

    public void printQrCodeAndSetDefaultPrinter(Integer printerId, Integer itemId, Integer pieces, Integer userId) throws Exception {
        Printer printer = printerRepository.findByPrinterId(printerId);
        if (printer != null) {
            PythonExecutor.printQrCodeWithPythonScript(itemId, printer.getPrinterIp(), printer.getPrinterModel(), pieces.toString());
            if ((departmentMemberRepository.findByUserId(userId) != null) &&
                    (departmentMemberRepository.findByUserId(userId).getPrinter() == null ||
                            (departmentMemberRepository.findByUserId(userId).getPrinter() != null && !departmentMemberRepository.findByUserId(userId).getPrinter().equals(printer)))) {
                DepartmentMember updatedDepartmentMember = departmentMemberRepository.findByUserId(userId);
                updatedDepartmentMember.setPrinter(printer);
                departmentMemberRepository.save(updatedDepartmentMember);
                System.out.println("###################################");
            }
        } else {
            throw new Exception("Printer " + printerId + " does not exist!");
        }
    }

    // ####################### Charts #######################

    public List<ChartItemDTO> getActivityChartItems() throws Exception {
        return getActivityChartItemsList(null);
    }

    public List<ChartItemDTO> getActivityChartItemsByDepartment(Integer departmentId) throws Exception {
        return getActivityChartItemsList(departmentId);
    }

    private List<ChartItemDTO> getActivityChartItemsList(Integer departmentId) throws Exception {
        int daysToSubtract = 14;
        List<ChartItemDTO> chartItemList = new ArrayList<>();
        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = LocalDateTime.now().minusDays(daysToSubtract);
        List<Change> changeList = changeRepository.findByDateRange(fromDate, toDate);
        for (int i = 0; i < daysToSubtract; i++) {
            fromDate = fromDate.plusDays(1);
            chartItemList.add(new ChartItemDTO(i, fromDate.toLocalDate()));
            for (Change change : changeList) {
                if (departmentId == null || departmentId.equals(change.getInventoryItem().getDepartment().getId())) {
                    if (change.getChangeDate().toLocalDate().equals(fromDate.toLocalDate())) {
                        if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand angelegt.")) {
                            chartItemList.get(i).setPiecesCreated(chartItemList.get(i).getPiecesCreated() + 1);
                        } else if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand bearbeitet.")) {
                            chartItemList.get(i).setPiecesManipulated(chartItemList.get(i).getPiecesManipulated() + 1);
                        } else if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand ausgegeben.")) {
                            chartItemList.get(i).setPiecesIssued(chartItemList.get(i).getPiecesIssued() + 1);
                        } else if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand ausgeschieden.")) {
                            chartItemList.get(i).setPiecesDropped(chartItemList.get(i).getPiecesDropped() + 1);
                        } else if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand aktiviert.")) {
                            chartItemList.get(i).setPiecesActivated(chartItemList.get(i).getPiecesActivated() + 1);
                        } else if (change.getChangeStatus().equalsIgnoreCase("Inventargegenstand deaktiviert.")) {
                            chartItemList.get(i).setPiecesDeactivated(chartItemList.get(i).getPiecesDeactivated() + 1);
                        } else {
                            throw new Exception("Änderungsstatus " + change.getChangeStatus() + " unbekannt!");
                        }
                    }
                }
            }
        }
        return chartItemList;
    }

    public List<ChartItemDTO> getTypeChartItems() {
        List<InventoryItem> itemList = inventoryItemRepository.findAll();
        return getTypeChartItemDTOS(itemList);
    }

    public List<ChartItemDTO> getTypeChartItemsByDepartment(Integer departmentId) {
        List<InventoryItem> itemList = inventoryItemRepository.findByDepartmentId(departmentId);
        return getTypeChartItemDTOS(itemList);
    }

    private List<ChartItemDTO> getTypeChartItemDTOS(List<InventoryItem> itemList) {
        List<Type> typeList = typeRepository.findAll();
        typeList.sort(Comparator.naturalOrder());
        List<ChartItemDTO> chartItemList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            chartItemList.add(new ChartItemDTO(i, typeList.get(i)));
            Set<Location> locationSet = new HashSet<>();
            Set<Department> departmentSet = new HashSet<>();
            for (InventoryItem item : itemList) {
                if (item.getType().equals(chartItemList.get(i).getType())) {
                    chartItemList.get(i).setPieces(chartItemList.get(i).getPieces() + item.getPieces());
                    chartItemList.get(i).setPiecesStored(chartItemList.get(i).getPiecesStored() + item.getPiecesStored());
                    chartItemList.get(i).setPiecesIssued(chartItemList.get(i).getPiecesIssued() + item.getPiecesIssued());
                    chartItemList.get(i).setPiecesDropped(chartItemList.get(i).getPiecesDropped() + item.getPiecesDropped());
                    locationSet.add(item.getLocation());
                    departmentSet.add(item.getDepartment());
                }
            }
            if (chartItemList.get(i).getPieces() != 0) {
                chartItemList.get(i).setLocations(createLocationsStringFromSet(new ArrayList<>(locationSet)));
                chartItemList.get(i).setDepartments(createDepartmentsStringFromSet(new ArrayList<>(departmentSet)));
            }
        }
        chartItemList.removeIf(item -> item.getPieces() == 0);
        return chartItemList;
    }

    private String createLocationsStringFromSet(ArrayList<Location> locationList) {
        StringBuilder sb = new StringBuilder();
        locationList.sort(Comparator.naturalOrder());
        int counter = 1;
        for (Location location : locationList) {
            sb.append(location.getLocationName());
            if (counter != locationList.size()) {
                sb.append(", ");
                counter++;
            }
        }

        return sb.toString();
    }

    private String createDepartmentsStringFromSet(ArrayList<Department> departmentList) {
        StringBuilder sb = new StringBuilder();
        departmentList.sort(Comparator.naturalOrder());
        int counter = 1;
        for (Department department : departmentList) {
            sb.append(department.getDepartmentName());
            if (counter != departmentList.size()) {
                sb.append(", ");
                counter++;
            }
        }
        return sb.toString();
    }

    public List<ChartItemDTO> getDepartmentItemChart() {
        List<InventoryItem> itemList = inventoryItemRepository.findAll();
        List<Department> departmentList = departmentRepository.findAll();
        List<ChartItemDTO> chartItemList = new ArrayList<>();
        for (int i = 0; i < departmentList.size(); i++) {
            chartItemList.add(new ChartItemDTO(i, departmentList.get(i)));
            for (InventoryItem item : itemList) {
                if (item.getDepartment().equals(chartItemList.get(i).getDepartment())) {
                    chartItemList.get(i).setPieces(chartItemList.get(i).getPieces() + item.getPieces());
                }
            }
        }
        return chartItemList;
    }

}

package net.inventorymanagement.inventorymanagementwebservice.service;

import net.inventorymanagement.inventorymanagementwebservice.dtos.ChartItemDTO;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import net.inventorymanagement.inventorymanagementwebservice.repositories.*;
import net.inventorymanagement.inventorymanagementwebservice.utils.DroppingQueueEnum;
import net.inventorymanagement.inventorymanagementwebservice.utils.PythonExecutor;
import net.inventorymanagement.inventorymanagementwebservice.utils.StatusEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class InventoryManagementService {

    @Value("${path.to.resources.folder}")
    private String pathToResourcesFolder;

    private final InventoryItemRepository inventoryItemRepository;
    private final TypeRepository typeRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final SupplierRepository supplierRepository;
    private final PictureRepository pictureRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMemberRepository departmentMemberRepository;
    private final PrinterRepository printerRepository;
    private final ChangeRepository changeRepository;

    @Autowired
    public InventoryManagementService(InventoryItemRepository inventoryItemRepository, TypeRepository typeRepository, CategoryRepository categoryRepository, LocationRepository locationRepository, SupplierRepository supplierRepository, PictureRepository pictureRepository, DepartmentRepository departmentRepository, DepartmentMemberRepository departmentMemberRepository, PrinterRepository printerRepository, ChangeRepository changeRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.typeRepository = typeRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.supplierRepository = supplierRepository;
        this.pictureRepository = pictureRepository;
        this.departmentRepository = departmentRepository;
        this.departmentMemberRepository = departmentMemberRepository;
        this.printerRepository = printerRepository;
        this.changeRepository = changeRepository;
    }

    // ####################### Inventory #######################

    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> itemList = inventoryItemRepository.findAll();
        itemList.sort(Comparator.naturalOrder());
        return itemList;
    }

    public InventoryItem getInventoryItemById(int id) {
        return inventoryItemRepository.findByItemId(id);
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

    public void revokeDroppingInventoryItem(InventoryItem inventoryItem) {
        resetDroppingQueueParameters(inventoryItem);
        inventoryItemRepository.saveAndFlush(inventoryItem);
    }

    public InventoryItem deactivateInventoryItem(int id) throws Exception {
        InventoryItem inventoryItemToDeactivate = inventoryItemRepository.findByItemId(id);
        if (!inventoryItemToDeactivate.isActive()) {
            throw new Exception("Der Inventargegenstand \"" + inventoryItemToDeactivate.getItemInternalNumber() + "\" ist bereits deaktiviert.");
        }
        if (!DroppingQueueEnum.DEAKTIVIEREN.toString().equals(inventoryItemToDeactivate.getDroppingQueue())) {
            throw new Exception("Für den Inventargegenstand \"" +
                    inventoryItemToDeactivate.getItemInternalNumber() +
                    "\" ist keine Deaktivierung angefordert.");
        }
        inventoryItemToDeactivate.setActive(false);
        inventoryItemToDeactivate.setStatus(StatusEnum.DEAKTIVIERT.name());
        resetDroppingQueueParameters(inventoryItemToDeactivate);
        return inventoryItemRepository.saveAndFlush(inventoryItemToDeactivate);
    }

    public InventoryItem activateInventoryItem(int id) throws Exception {
        InventoryItem inventoryItemToActivate = inventoryItemRepository.findByItemId(id);
        if (!inventoryItemToActivate.isActive()) {
            inventoryItemToActivate.setActive(true);
            inventoryItemToActivate.setStatus(getItemStatus(inventoryItemToActivate));
        } else {
            throw new Exception("Der Inventargegenstand \"" + inventoryItemToActivate.getItemInternalNumber() + "\" ist bereits aktiviert.");
        }
        return inventoryItemRepository.saveAndFlush(inventoryItemToActivate);
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
        pictureRepository.saveAndFlush(model);
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

    private void resetDroppingQueueParameters(InventoryItem inventoryItem) {
        inventoryItem.setDroppingQueue(null);
        inventoryItem.setDroppingQueuePieces(null);
        inventoryItem.setDroppingQueueReason(null);
        inventoryItem.setDroppingQueueRequester(null);
        inventoryItem.setDroppingQueueDate(null);
    }

    private String getItemStatus(InventoryItem item) {
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

    // ####################### Search #######################

    public List<InventoryItem> quickSearchAllDepartments(String search) {
        String[] searchTerms = splitSearchStringBySpace(search);
        Map<InventoryItem, Integer> itemCountMap = new HashMap<>();
        for (String term : searchTerms) {
            List<InventoryItem> items = inventoryItemRepository.findBySearchString(term);
            for (InventoryItem item : items) {
                itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + 1);
            }
        }
        return convertMapToListAndSort(itemCountMap);
    }

    public List<InventoryItem> quickSearchByDepartment(String search, int departmentId) {
        String[] searchTerms = splitSearchStringBySpace(search);
        Map<InventoryItem, Integer> itemCountMap = new HashMap<>();
        for (String term : searchTerms) {
            List<InventoryItem> items = inventoryItemRepository.findBySearchStringAndDepartmentId(term, departmentId);
            for (InventoryItem item : items) {
                itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + 1);
            }
        }
        return convertMapToListAndSort(itemCountMap);
    }

    private static String[] splitSearchStringBySpace(String search) {
        return Arrays.stream(search.split("\\s+")) // split by space
                .filter(str -> str.length() > 1) // only include strings with more than one character
                .toArray(String[]::new);
    }

    private static List<InventoryItem> convertMapToListAndSort(Map<InventoryItem, Integer> itemCountMap) {
        List<Map.Entry<InventoryItem, Integer>> itemList = new ArrayList<>(itemCountMap.entrySet());
        itemList.sort(Map.Entry.comparingByKey()); // sort by internal inventory number first
        itemList.sort((entry1, entry2) -> { // then sort by hit counter
            int countCompare = entry2.getValue().compareTo(entry1.getValue());
            if (countCompare == 0) {
                return ((Comparable<InventoryItem>) entry1.getKey()).compareTo(entry2.getKey());
            }
            return countCompare;
        });
        return itemList.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    // ####################### Export #######################

    public String getFilterString(Integer departmentId, Integer categoryId, Integer typeId, Integer locationId, Integer supplierId, String status,
                                  LocalDateTime creationDateFrom, LocalDateTime creationDateTo, LocalDateTime deliveryDateFrom, LocalDateTime deliveryDateTo,
                                  LocalDateTime issueDateFrom, LocalDateTime issueDateTo, LocalDateTime droppingDateFrom, LocalDateTime droppingDateTo) {
        StringBuilder sb = new StringBuilder();
        if (departmentId != null) {
            Department department = departmentRepository.findByDepartmentId(departmentId);
            if (department != null) {
                sb.append("Abteilung: ");
                sb.append(department.getDepartmentName());
                sb.append(", ");
            }
        }
        if (categoryId != null) {
            Category category = categoryRepository.findByCategoryId(categoryId);
            if (category != null) {
                sb.append("Kategorie: ");
                sb.append(category.getCategoryName());
                sb.append(", ");
            }
        }
        if (typeId != null) {
            Type type = typeRepository.findByTypeId(typeId);
            if (type != null) {
                sb.append("Typ: ");
                sb.append(type.getTypeName());
                sb.append(", ");
            }
        }
        if (locationId != null) {
            Location location = locationRepository.findByLocationId(locationId);
            if (location != null) {
                sb.append("Standort: ");
                sb.append(location.getLocationName());
                sb.append(", ");
            }
        }
        if (supplierId != null) {
            Supplier supplier = supplierRepository.findBySupplierId(supplierId);
            if (supplier != null) {
                sb.append("Lieferant: ");
                sb.append(supplier.getSupplierName());
                sb.append(", ");
            }
        }
        if (status != null) {
            sb.append("Status: ");
            sb.append(status);
            sb.append(", ");
        }
        if (creationDateFrom != null || creationDateTo != null) {
            sb.append("Anlagedatum von ");
            getDateRangeString(creationDateFrom, creationDateTo, sb);
        }
        if (deliveryDateFrom != null || deliveryDateTo != null) {
            sb.append("Lieferdatum von ");
            getDateRangeString(deliveryDateFrom, deliveryDateTo, sb);
        }
        if (issueDateFrom != null || issueDateTo != null) {
            sb.append("Ausgabedatum von ");
            getDateRangeString(issueDateFrom, issueDateTo, sb);
        }
        if (droppingDateFrom != null || droppingDateTo != null) {
            sb.append("Ausscheidedatum von ");
            getDateRangeString(droppingDateFrom, droppingDateTo, sb);
        }
        if (sb.toString().isEmpty()) {
            return "-";
        } else {
            return sb.substring(0, sb.toString().length() - 2);
        }
    }

    private void getDateRangeString(LocalDateTime fromDate, LocalDateTime toDate, StringBuilder sb) {
        if (fromDate != null) {
            sb.append(fromDate.toLocalDate());
        } else {
            sb.append("*");
        }
        sb.append(" bis ");
        if (toDate != null) {
            sb.append(toDate.toLocalDate());
        } else {
            sb.append("*");
        }
        sb.append(", ");
    }

    // ####################### Item name #######################

    public List<String> getAllItemNamesByDepartment(Integer departmentId) {
        if (departmentId == 0) {
            return inventoryItemRepository.findAllItemNames();
        } else {
            return inventoryItemRepository.findAllItemNamesByDepartmentId(departmentId);
        }
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
            typeRepository.saveAndFlush(Objects.requireNonNull(checkIfTypeAlreadyExists(type)));
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
        categoryRepository.saveAndFlush(Objects.requireNonNull(checkICategoryAlreadyExists(category)));
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
        locationRepository.saveAndFlush(Objects.requireNonNull(checkIfLocationAlreadyExists(location)));
    }

    private Location checkIfLocationAlreadyExists(Location location) throws Exception {
        Location locationDuplicate = locationRepository.findByLocationName(location.getLocationName());
        if (locationDuplicate != null) {
            throw new Exception("Standort \"" + locationDuplicate.getLocationName() + "\" existiert bereits!");
        }
        return location;
    }

    // ####################### Room #######################

    public List<String> getAllRoomsByDepartment(Integer departmentId) {
        if (departmentId == 0) {
            return inventoryItemRepository.findAllRooms();
        } else {
            return inventoryItemRepository.findAllRoomsByDepartmentId(departmentId);
        }
    }

    // ####################### Supplier #######################

    public List<Supplier> getAllSuppliers() {
        List<Supplier> supplierList = supplierRepository.findAll();
        supplierList.sort(Comparator.naturalOrder());
        return supplierList;
    }

    public void addSupplier(Supplier supplier) throws Exception {
        supplierRepository.saveAndFlush(Objects.requireNonNull(checkIfSupplierAlreadyExists(supplier)));
    }

    private Supplier checkIfSupplierAlreadyExists(Supplier supplier) throws Exception {
        Supplier supplierDuplicate = supplierRepository.findBySupplierName(supplier.getSupplierName());
        if (supplierDuplicate != null) {
            throw new Exception("Lieferant \"" + supplierDuplicate.getSupplierName() + "\" existiert bereits!");
        }
        return supplier;
    }

    // ####################### Pictures #######################

    public Picture getPicture(Integer id) {
        var picture = pictureRepository.findById(id);
        return picture.orElse(null);
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
        departmentRepository.saveAndFlush(Objects.requireNonNull(checkIfDepartmentAlreadyExists(department)));
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

    public DepartmentMember getDepartmentMemberByUserId(Integer id) {
        var member = departmentMemberRepository.findByUserId(id);
        // there MUST be at least two people per department who can drop review, so the property will be overwritten if there aren't
        if (!member.isDroppingReviewer()) {
            var members = getAllDepartmentMembersFromDepartmentId(member.getDepartment().getId());
            var count = members.stream().filter(DepartmentMember::isDroppingReviewer).count();
            if (count <= 1) {
                member.setDroppingReviewer(true);
            }
        }
        return member;
    }

    public void updateDepartmentMember(DepartmentMember member) {
        departmentMemberRepository.saveAndFlush(member);
    }

    public Department addDepartmentMemberToDepartment(Integer departmentId, Integer userId) throws Exception {
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if (department != null) {
            departmentMemberRepository.saveAndFlush(Objects.requireNonNull(checkIfDepartmentMemberIsAlreadyAssignedToADepartment(userId, department)));
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
        changeRepository.saveAndFlush(change);
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
        printerRepository.saveAndFlush(Objects.requireNonNull(checkIfPrinterAlreadyExists(printer)));
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
            PythonExecutor.printQrCodeWithPythonScript(pathToResourcesFolder, itemId, printer.getPrinterIp(), printer.getPrinterModel(), pieces.toString());
            if ((departmentMemberRepository.findByUserId(userId) != null) &&
                    (departmentMemberRepository.findByUserId(userId).getPrinter() == null ||
                            (departmentMemberRepository.findByUserId(userId).getPrinter() != null && !departmentMemberRepository.findByUserId(userId).getPrinter().equals(printer)))) {
                DepartmentMember updatedDepartmentMember = departmentMemberRepository.findByUserId(userId);
                updatedDepartmentMember.setPrinter(printer);
                departmentMemberRepository.saveAndFlush(updatedDepartmentMember);
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

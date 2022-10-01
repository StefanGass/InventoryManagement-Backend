package net.inventorymanagement.service;

import net.inventorymanagement.model.*;
import net.inventorymanagement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    private ChangeRepository changeRepository;

    // ####################### Inventory #######################

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryItemRepository.findAll();
    }

    public List<InventoryItem> getInventoryItemsByDepartmentId(Integer departmentId) throws Exception {
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if (department != null) {
            return inventoryItemRepository.findByDepartmentId(departmentId);
        } else {
            throw new Exception("Department \"" + departmentId + "\" doesn't exist!");
        }
    }

    public InventoryItem addInventory(InventoryItem model) throws Exception {
        return inventoryItemRepository.save(checkIfInventoryItemAlreadyExists(model));
    }

    private InventoryItem checkIfInventoryItemAlreadyExists(InventoryItem item) throws Exception {
        InventoryItem itemDuplicate = inventoryItemRepository.findByItemInternalNumber(item.getItemInternalNumber());
        if (itemDuplicate != null) {
            throw new Exception("Inventory number \"" + itemDuplicate.getItemInternalNumber() + "\" already in use!");
        }
        return item;
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
        throw new Exception("Category not found!");
    }

    private Type checkIfTypeAlreadyExists(Type type) throws Exception {
        Type typeDuplicate = typeRepository.findByTypeName(type.getTypeName());
        if (typeDuplicate != null) {
            throw new Exception("Type \"" + typeDuplicate.getTypeName() + "\" already exists!");
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
        categoryRepository.save(Objects.requireNonNull(checkICategoryAlreadyExists(category)));
    }

    private Category checkICategoryAlreadyExists(Category category) throws Exception {
        Category categoryDuplicate = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryDuplicate != null) {
            throw new Exception("Category \"" + categoryDuplicate.getCategoryName() + "\" already exists!");
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
            throw new Exception("Location \"" + locationDuplicate.getLocationName() + "\" already exists!");
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
            throw new Exception("Supplier \"" + supplierDuplicate.getSupplierName() + "\" already exists!");
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
                throw new Exception("User \"" + userId + "\" has no department assigned!");
            }
        } else {
            throw new Exception("User \"" + userId + "\" does not exist or has no department assigned!");
        }
    }

    public void addDepartment(Department department) throws Exception {
        departmentRepository.save(Objects.requireNonNull(checkIfDepartmentAlreadyExists(department)));
    }

    private Department checkIfDepartmentAlreadyExists(Department department) throws Exception {
        Department departmentDuplicate = departmentRepository.findByDepartmentName(department.getDepartmentName());
        if (departmentDuplicate != null) {
            throw new Exception("Department \"" + departmentDuplicate.getDepartmentName() + "\" already exists!");
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
            throw new Exception("Department does not exist!");
        }
    }

    private DepartmentMember checkIfDepartmentMemberIsAlreadyAssignedToADepartment(Integer userId, Department department) throws Exception {
        DepartmentMember departmentMemberDuplicate = departmentMemberRepository.findByUserId(userId);
        if (departmentMemberDuplicate != null) {
            throw new Exception("User \"" + departmentMemberDuplicate.getUserId() + "\" already has a department assigned! Users can generally only have one department at a time.");
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
                throw new Exception("User \"" + userId + "\" doesn't exist!");
            }
        } else {
            throw new Exception("Department \"" + departmentId + "\" doesn't exist!");
        }
    }

    // ####################### Inventory Item #######################

    public InventoryItem getInventoryItemById(String id) {
        return inventoryItemRepository.getById(Integer.valueOf(id));
    }

    public InventoryItem deactivateInventoryItem(String id) throws Exception {
        InventoryItem inventoryItemToDeactivate = getInventoryItemById(id);
        if (inventoryItemToDeactivate.isActive()) {
            inventoryItemToDeactivate.setActive(false);
        } else {
            throw new Exception("Item \"" + inventoryItemToDeactivate.getItemInternalNumber() + "\" is already set inactive.");
        }
        return inventoryItemRepository.save(inventoryItemToDeactivate);
    }


    public InventoryItem activateInventoryItem(String id) throws Exception {
        InventoryItem inventoryItemToActivate = getInventoryItemById(id);
        if (!inventoryItemToActivate.isActive()) {
            inventoryItemToActivate.setActive(true);
        } else {
            throw new Exception("Item \"" + inventoryItemToActivate.getItemInternalNumber() + "\" is already set active.");
        }
        return inventoryItemRepository.save(inventoryItemToActivate);
    }

    // ####################### Change #######################

    public Change addChange(Change change) {
        return changeRepository.save(change);
    }

}

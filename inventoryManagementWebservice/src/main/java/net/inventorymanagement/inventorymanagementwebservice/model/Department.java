package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "department")
@Getter
@Setter
@ToString
public class Department implements Comparable<Department> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String departmentName;
    @OneToMany(mappedBy = "department")
    @JsonManagedReference
    @ToString.Exclude
    private List<DepartmentMember> departmentMembers;

    @Override
    public int compareTo(Department o) {
        return this.getDepartmentName().compareToIgnoreCase(o.getDepartmentName());
    }

}

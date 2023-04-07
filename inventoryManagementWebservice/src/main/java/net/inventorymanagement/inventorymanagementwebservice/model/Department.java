package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.*;
import java.util.*;
import javax.persistence.*;
import lombok.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "department")
@Getter
@Setter
@ToString
@Indexed
public class Department implements Comparable<Department> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "departmentId")
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

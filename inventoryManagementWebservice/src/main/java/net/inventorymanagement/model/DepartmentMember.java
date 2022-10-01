package net.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "department_member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DepartmentMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "department_id")
    private Department department;

    public DepartmentMember(int userId, Department department) {
        this.userId = userId;
        this.department = department;
    }

}

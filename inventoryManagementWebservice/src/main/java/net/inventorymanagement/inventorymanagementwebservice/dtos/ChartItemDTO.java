package net.inventorymanagement.inventorymanagementwebservice.dtos;

import net.inventorymanagement.inventorymanagementwebservice.model.Department;
import net.inventorymanagement.inventorymanagementwebservice.model.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ChartItemDTO {

    private Integer id;
    private Type type;
    private Department department;
    private Integer pieces;
    private Integer piecesStored;
    private Integer piecesIssued;
    private Integer piecesDropped;

    private String locations;
    private String departments;

    private LocalDate processingDate;
    private Integer piecesCreated;
    private Integer piecesManipulated;
    private Integer piecesActivated;
    private Integer piecesDeactivated;

    public ChartItemDTO(Integer id, Type type) {
        this.id = id;
        this.type = type;
        this.pieces = 0;
        this.piecesStored = 0;
        this.piecesIssued = 0;
        this.piecesDropped = 0;
    }

    public ChartItemDTO(Integer id, Department department) {
        this.id = id;
        this.department = department;
        this.pieces = 0;
    }

    public ChartItemDTO(Integer id, LocalDate processingDate) {
        this.id = id;
        this.processingDate = processingDate;
        this.piecesCreated = 0;
        this.piecesManipulated = 0;
        this.piecesIssued = 0;
        this.piecesDropped = 0;
        this.piecesActivated = 0;
        this.piecesDeactivated = 0;
    }

}

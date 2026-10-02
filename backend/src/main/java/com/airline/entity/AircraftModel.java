package com.airline.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "aircraft_model")
public class AircraftModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "manufacturer", nullable = false, length = 50)
    private String manufacturer;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    public AircraftModel() {}

    public AircraftModel(String manufacturer, String modelName, Integer capacity) {
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.capacity = capacity;
    }

    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}

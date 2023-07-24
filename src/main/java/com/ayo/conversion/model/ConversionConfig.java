package com.ayo.conversion.model;

import javax.persistence.*;

/**
 * This class represents a Conversion config.
 * It contains properties for the source unit, the target unit, the conversion
 * factor and addend.
 */
@Entity
@Table(name = "conversions")
public class ConversionConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_unit")
    private String fromUnit;

    @Column(name = "to_unit")
    private String toUnit;

    @Column(name = "conversion_factor")
    private double convertFactor;

    @Column(name = "addend")
    private Double addend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    public double getConvertFactor() {
        return convertFactor;
    }

    public void setConvertFactor(double convertFactor) {
        this.convertFactor = convertFactor;
    }

    public Double getAddend() {
        return addend;
    }

    public void setAddend(Double addend) {
        this.addend = addend;
    }

    @Override
    public String toString() {
        return "Conversion{" +
                "id=" + id +
                ", fromUnit='" + fromUnit + '\'' +
                ", toUnit='" + toUnit + '\'' +
                ", convertFactor=" + convertFactor +
                '}';
    }
}

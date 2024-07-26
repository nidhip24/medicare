package com.og.medicare.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Distribution {
    private int id;
    private int quantity_dispensed;
    private String generic_name;
    private String brand_name;
    private String unit_of_measure;
    private String lot_number;
    private String patient_name;
    private String patient_address;
    private String patient_diagnosis;
    private Date date_dispensed;
    private Date expiration_date;
    private Date patient_birth_date;
    private Date created_at;
}

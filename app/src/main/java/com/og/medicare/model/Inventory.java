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
public class Inventory {

    int id;
    int addedByUserId;
    float quantity;
    String medicineName;
    String brandName;
    String lotNumber;
    String unitOfMeasurement;
    Date expiryDate;
}

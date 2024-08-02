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
public class Order {
    private int id;
    private int mtid;
    private int quantity_requested;
    private String requested_by;
    private String health_station_name;
    private String added_by;
    private Date date_requested;
    private Date created_at;
}

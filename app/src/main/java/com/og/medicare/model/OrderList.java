package com.og.medicare.model;

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
public class OrderList {

    int id;
    String title;
    String subTitle;
    String addedBy;
    String status;
    Object obj;
    boolean isAdmin;
}

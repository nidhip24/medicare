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
public class User {
    private int id;
    private int rid;
    private String name;
    private String email;
    private String healthstation;
    private Date created_at;
}

package com.example.registerapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {
    private String name;
    private String phone;
    private String email;
    private String password;
}

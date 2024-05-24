package com.example.registerapp.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String name;

    private String password;
    private String phone;
}

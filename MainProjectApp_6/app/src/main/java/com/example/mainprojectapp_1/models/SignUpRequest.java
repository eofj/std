package com.example.mainprojectapp_1.models;

public class SignUpRequest {
    private String name;
    private String id;
    private String password;

    public SignUpRequest(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }
}

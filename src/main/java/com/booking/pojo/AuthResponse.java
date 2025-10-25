package com.booking.pojo;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String error;
}

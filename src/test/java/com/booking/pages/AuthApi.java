package com.booking.pages;

import com.booking.pojo.AuthRequest;
import com.booking.pojo.AuthResponse;
import com.booking.utils.ConfigReader;
import io.restassured.RestAssured;

import java.util.Objects;

import static io.restassured.RestAssured.given;

public class AuthApi {

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String USERNAME = ConfigReader.get("username");
    private static final String PASSWORD = ConfigReader.get("password");

    public AuthApi() {
        RestAssured.baseURI = BASE_URL;
    }

    // Public method to get token, branchless
    public String generateToken() {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD);
        AuthResponse response = given()
                .header("Content-Type", "application/json")
                .body(request)
                .post("/auth/login")
                .as(AuthResponse.class);

        // branchless null check
        return Objects.requireNonNull(response.getToken(),
                () -> "Failed to generate token. Error: " + response.getError());
    }
}

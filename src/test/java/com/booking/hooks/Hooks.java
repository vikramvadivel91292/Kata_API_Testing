package com.booking.hooks;

import com.booking.pages.AuthApi;
import io.cucumber.java.Before;

public class Hooks {
    public static String token;

    @Before(order = 0)
    public void generateToken() {
        AuthApi authApi = new AuthApi();
        token = authApi.generateToken();
    }
}

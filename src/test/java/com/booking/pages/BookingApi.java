package com.booking.pages;

import com.booking.pojo.BookingRequest;
import com.booking.pojo.BookingResponse;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import com.booking.utils.ConfigReader;

public class BookingApi {

    private static final String BASE_URL = ConfigReader.get("baseUrl");

    public BookingApi() {
        RestAssured.baseURI = BASE_URL;
    }

    // Create booking and return deserialized BookingResponse object
    public BookingResponse createBooking(BookingRequest bookingRequest, String token) {
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(bookingRequest)
                .log().all()
                .post("/booking")
                .then()
                .log().all()
                .statusCode(201) // basic validation, optional to remove
                .extract()
                .as(BookingResponse.class);
    }
}

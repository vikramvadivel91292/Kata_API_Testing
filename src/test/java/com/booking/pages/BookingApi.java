package com.booking.pages;

import com.booking.pojo.BookingRequest;
import com.booking.pojo.BookingResponse;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import com.booking.utils.ConfigReader;
import io.restassured.response.Response;

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

    public Response createBookingRaw(BookingRequest request, String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(request)
                .log().all()
                .when()
                .post("/booking")
                .then()
                .log().all()
                .extract().response();
    }

    public Response getBookingById(int id, String token) {
        return given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + id)
                .header("Cookie", "token=" + token)
                .when()
                .get()
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deleteBooking(int bookingId, String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .log().all()
                .extract().response();
    }
}

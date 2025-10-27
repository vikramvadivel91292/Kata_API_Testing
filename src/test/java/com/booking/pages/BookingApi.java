package com.booking.pages;

import com.booking.pojo.BookingRequest;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import com.booking.utils.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BookingApi {

    private static final String BASE_URL = ConfigReader.get("baseUrl");

    public BookingApi() {
        RestAssured.baseURI = BASE_URL;
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

    public Response updateBooking(int bookingId, BookingRequest bookingRequest, String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(bookingRequest)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .log().all()
                .extract().response();
    }

    public Response updateBookingRaw(int bookingId, BookingRequest bookingRequest, String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(bookingRequest)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .log().all()
                .extract().response();
    }

    public Response partialUpdateBooking(int bookingId, BookingRequest bookingRequest, String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(bookingRequest)
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .log().all()
                .extract().response();
    }
}

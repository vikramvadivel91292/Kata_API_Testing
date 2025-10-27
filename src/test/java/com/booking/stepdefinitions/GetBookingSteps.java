package com.booking.stepdefinitions;

import com.booking.hooks.Hooks;
import com.booking.pages.BookingApi;
import com.booking.pojo.BookingResponse;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import lombok.Setter;
import org.testng.Assert;

public class GetBookingSteps {

    private BookingApi bookingApi;
    private Response response;
    private int bookingId;
    private String token;

    // This method allows other step definitions to pass the created booking
    // This holds the booking response from "create booking"
    @Setter
    private static BookingResponse bookingResponse;

    @Given("user has a booking ID {int}")
    public void user_has_a_booking_ID(Integer id) {
        this.bookingId = id;
        this.token = Hooks.token; // default valid token
    }

    @When("user sends GET request for booking")
    public void user_sends_GET_request_for_booking() {
        bookingApi = new BookingApi();
        response = bookingApi.getBookingById(bookingId, token);
    }

    @When("user sends GET request for booking with token {string}")
    public void user_sends_GET_request_for_booking_with_token(String providedToken) {
        bookingApi = new BookingApi();
        response = bookingApi.getBookingById(bookingId, providedToken);
    }

    @Then("response status code should be {int}")
    public void response_status_code_should_be(Integer expectedStatusCode) {
        int actualStatus = response.getStatusCode();
        Assert.assertEquals(actualStatus, expectedStatusCode.intValue(),
                "Status code mismatch! Actual: " + actualStatus + "\nResponse: " + response.getBody().asString());
        System.out.println("Verified Status Code: " + actualStatus);
    }

    @Then("booking details should be returned successfully")
    public void booking_details_should_be_returned_successfully() {
        BookingResponse booking = response.as(BookingResponse.class);

        Assert.assertNotNull(booking.getFirstname(), "Firstname should not be null");
        Assert.assertNotNull(booking.getLastname(), "Lastname should not be null");
        Assert.assertNotNull(booking.getBookingdates(), "Booking dates should not be null");

        System.out.println("Booking retrieved for: " + booking.getFirstname() + " " + booking.getLastname());
    }

    @Then("response should contain error message {string}")
    public void response_should_contain_error_message(String expectedError) {
        String actualError = response.jsonPath().getString("error");
        Assert.assertTrue(
                actualError != null && actualError.equalsIgnoreCase(expectedError),
                "Error mismatch! Expected: " + expectedError + ", Actual: " + actualError
        );
        System.out.println("Verified error: " + actualError);
    }

    // CHAINED GET REQUEST (uses bookingResponse from create)
    @When("user sends GET request for same booking ID")
    public void user_sends_GET_request_for_same_booking_ID() {
        Assert.assertNotNull(bookingResponse, "Booking response is null — ensure booking was created first!");

        int bookingId = bookingResponse.getBookingid();
        Assert.assertTrue(bookingId > 0, "Invalid booking ID returned from create booking response!");

        bookingApi = new BookingApi();
        response = bookingApi.getBookingById(bookingId, Hooks.token);

        BookingResponse getBookingResponse = response.as(BookingResponse.class);

        System.out.println("Retrieved Booking Details for ID: " + bookingId);
        System.out.println("GET Response: " + getBookingResponse);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch!");
        Assert.assertEquals(getBookingResponse.getBookingid(), bookingId, "Booking ID mismatch between POST and GET!");

        Assert.assertEquals(getBookingResponse.getFirstname(), bookingResponse.getFirstname(), "First name mismatch!");
        Assert.assertEquals(getBookingResponse.getLastname(), bookingResponse.getLastname(), "Last name mismatch!");
        Assert.assertEquals(getBookingResponse.getRoomid(), bookingResponse.getRoomid(), "Room ID mismatch!");
    }
}

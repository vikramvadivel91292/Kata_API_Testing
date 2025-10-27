package com.booking.stepdefinitions;

import com.booking.hooks.Hooks;
import com.booking.pages.BookingApi;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;

public class DeleteBookingSteps {

    private BookingApi bookingApi;
    private Response response;
    private int bookingId;
    private String token;

    @Given("user has a valid booking ID {int}")
    public void user_has_a_valid_booking_ID(Integer id) {
        Assert.assertNotNull(Hooks.token, "Token should not be null");
        System.out.println("Auth Token verified: " + Hooks.token);
        bookingId = id;
        this.token = Hooks.token;
        System.out.println("Booking ID to delete: " + bookingId);
    }

    @When("user sends DELETE request for that booking")
    public void user_sends_DELETE_request_for_that_booking() {
        bookingApi = new BookingApi();
        response = bookingApi.deleteBooking(bookingId, token);
        System.out.println("DELETE Response: " + response.getBody().asString());
    }

    @When("user sends DELETE request with invalid token {string}")
    public void user_sends_DELETE_request_with_invalid_token(String invalidToken) {
        bookingApi = new BookingApi();
        response = bookingApi.deleteBooking(bookingId, invalidToken);
        System.out.println("DELETE Response (Invalid Token): " + response.getBody().asString());
    }

    @Then("delete response status code should be {int}")
    public void delete_response_status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode.intValue(),
                "Unexpected status code!");
        System.out.println("Verified DELETE status code: " + response.getStatusCode());
    }

    @Then("booking should be deleted successfully")
    public void booking_should_be_deleted_successfully() {
        Assert.assertEquals(response.getStatusCode(), 201, "Booking not deleted!");
        System.out.println("Booking deleted successfully for ID: " + bookingId);
    }

    @Then("delete response should contain error message {string}")
    public void delete_response_should_contain_error_message(String expectedError) {
        String actualResponse = response.getBody().asString();
        Assert.assertTrue(actualResponse.contains(expectedError),
                "Expected error message not found! Actual response: " + actualResponse);
        System.out.println("Verified error message: " + expectedError);
    }
}

package com.booking.stepdefinitions;

import com.booking.hooks.Hooks;
import com.booking.pages.BookingApi;
import com.booking.pojo.BookingRequest;
import com.booking.pojo.BookingResponse;
import com.booking.utils.JsonUtils;
import io.cucumber.java.en.*;
import org.testng.Assert;
import java.io.IOException;

public class CreateBookingSteps {

    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;

    @Given("user have valid authentication token")
    public void user_have_valid_authentication_token() {
        Assert.assertNotNull(Hooks.token, "Token should not be null");
        System.out.println("Auth Token verified: " + Hooks.token);
    }

    @Given("user have valid booking data")
    public void user_have_valid_booking_data() throws IOException {
        bookingRequest = JsonUtils.loadBookingData("src/test/resources/testdata/bookingData.json");
        Assert.assertNotNull(bookingRequest, "Booking data should be loaded from JSON");
        System.out.println("Booking data loaded: " + bookingRequest.getFirstname());
    }

    @When("user sends POST request to create a booking")
    public void user_sends_POST_request_to_create_a_booking() {
        BookingApi bookingApi = new BookingApi();
        bookingResponse = bookingApi.createBooking(bookingRequest, Hooks.token);
    }

    @Then("user should get valid booking response with status code {int}")
    public void user_should_get_valid_booking_response_with_status_code(Integer statusCode) {
        Assert.assertEquals((int) statusCode, 201, "Status code mismatch");
        Assert.assertTrue(bookingResponse.getBookingid() > 0, "Booking ID should be greater than 0");
        Assert.assertEquals(bookingResponse.getFirstname(), bookingRequest.getFirstname(), "Firstname mismatch");
        Assert.assertEquals(bookingResponse.getLastname(), bookingRequest.getLastname(), "Lastname mismatch");
        Assert.assertEquals(bookingResponse.getRoomid(), bookingRequest.getRoomid(), "Room ID mismatch");

        Assert.assertEquals(
                bookingResponse.getBookingdates().getCheckin(),
                bookingRequest.getBookingdates().getCheckin(),
                "Check-in date mismatch"
        );
        Assert.assertEquals(
                bookingResponse.getBookingdates().getCheckout(),
                bookingRequest.getBookingdates().getCheckout(),
                "Check-out date mismatch"
        );

        System.out.println("Booking successfully created with ID: " + bookingResponse.getBookingid());
        System.out.println("Check-in/out dates verified: "
                + bookingResponse.getBookingdates().getCheckin()
                + " → "
                + bookingResponse.getBookingdates().getCheckout());
    }
}

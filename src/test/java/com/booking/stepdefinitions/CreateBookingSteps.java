package com.booking.stepdefinitions;

import com.booking.hooks.Hooks;
import com.booking.pages.BookingApi;
import com.booking.pojo.BookingDates;
import com.booking.pojo.BookingRequest;
import com.booking.pojo.BookingResponse;
import com.booking.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreateBookingSteps {

    private BookingApi bookingApi;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private Response response;

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
        bookingApi = new BookingApi();
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

        // Share booking response with GetBookingSteps
        GetBookingSteps.setBookingResponse(bookingResponse);
    }

    // For Negative Scenarios
    @When("user send a POST request to create booking with")
    public void user_send_a_POST_request_to_create_booking_with(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        bookingRequest = new BookingRequest();

        // Handle complex roomid parsing
        bookingRequest.setRoomidRaw(parseRoomId(data.get("roomid")));
        // Handle firstname as Object: string, JSON object, array, number, boolean, etc.
        String firstnameInput = data.get("firstname");
        bookingRequest.setFirstnameRaw(parseJsonValue(firstnameInput));
        String lastnameInput = data.get("lastname");
        bookingRequest.setLastnameRaw(parseJsonValue(lastnameInput));

        // Handle flexible boolean parsing for depositpaid
        bookingRequest.setDepositpaidRaw(parseDeposit(data.get("depositpaid")));

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin(data.get("checkin"));
        bookingDates.setCheckout(data.get("checkout"));
        bookingRequest.setBookingdates(bookingDates);

        bookingRequest.setEmail(data.get("email"));
        bookingRequest.setPhone(data.get("phone"));

        bookingApi = new BookingApi();
        response = bookingApi.createBookingRaw(bookingRequest, Hooks.token);
    }


    @Then("The response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals(
                response.getStatusCode(),
                expectedStatusCode.intValue(),
                "Unexpected status code! Response: " + response.getBody().asString()
        );
        System.out.println("Verified status code: " + expectedStatusCode);
    }

    @Then("The response should contain error {string}")
    public void the_response_should_contain_error(String expectedErrors) {
        // Extract actual errors as List<String> from JSON
        List<String> actualErrors = response.jsonPath().getList("errors", String.class);
        Assert.assertNotNull(actualErrors, "'errors' field is missing in response!");

        // Split expected errors (comma-separated from Examples)
        String[] expectedList = expectedErrors.split(",");

        // Normalize whitespace for safety
        List<String> expectedTrimmed = Arrays.stream(expectedList)
                .map(String::trim)
                .toList();

        System.out.println("Actual errors: " + actualErrors);
        System.out.println("Expected errors: " + expectedTrimmed);

        // Check if all expected errors are present
        for (String expected : expectedTrimmed) {
            boolean matchFound = actualErrors.stream()
                    .anyMatch(actual -> actual.toLowerCase().contains(expected.toLowerCase()));

            Assert.assertTrue(
                    matchFound,
                    "Expected error not found!\nMissing: " + expected + "\nActual errors: " + actualErrors
            );
            System.out.println("Verified error: " + expected);
        }
    }

    private Object parseRoomId(String value) {
        if (isNullOrBlank(value)) return null;

        try {
            // Try to parse integer
            return Integer.parseInt(value);
        } catch (NumberFormatException e1) {
            try {
                // Try to parse decimal (e.g., 11.2)
                return Double.parseDouble(value);
            } catch (NumberFormatException e2) {
                // Handle booleans like true/false
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(value);
                }
                // Keep invalid strings (e.g., "abc")
                return value;
            }
        }
    }

    // Parse depositpaid — allows true/false, numbers, strings, or invalid data
    private Object parseDeposit(String value) {
        if (isNullOrBlank(value)) return null;

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        try {
            // Handle numeric input like 1, 0, 21
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value; // Keep as string for invalid cases
        }
    }

    // Helper to parse any JSON-like string into proper Object
    private Object parseJsonValue(String value) {
        if (isNullOrBlank(value)) return null;

        value = value.trim();

        // Try parsing JSON
        if (value.startsWith("{") || value.startsWith("[") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.matches("-?\\d+(\\.\\d+)?")) {
            try {
                return new ObjectMapper().readValue(value, Object.class);
            } catch (IOException e) {
                // Fallback to string if parsing fails
                return value;
            }
        }

        // Otherwise, treat as normal string
        return value;
    }

    // Utility to check for null, empty, or literal "null"
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null");
    }
}

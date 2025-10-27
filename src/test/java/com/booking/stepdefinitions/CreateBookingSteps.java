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
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateBookingSteps {

    private static final Logger logger = LogManager.getLogger(CreateBookingSteps.class);

    private BookingApi bookingApi;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private Response response;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("user have valid authentication token")
    public void user_have_valid_authentication_token() {
        Assert.assertNotNull(Hooks.token, "Token should not be null");
        logger.info("Auth Token verified: {}", Hooks.token);
    }

    @Given("user have valid booking data")
    public void user_have_valid_booking_data() throws IOException {
        bookingRequest = JsonUtils.loadBookingData("src/test/resources/testdata/bookingData.json");
        Assert.assertNotNull(bookingRequest, "Booking data should be loaded from JSON");
        logger.info("Booking data loaded with firstname: {}", bookingRequest.getFirstname());
    }

    @When("user sends POST request to create a booking")
    public void user_sends_POST_request_to_create_a_booking() {
        bookingApi = new BookingApi();
        response = bookingApi.createBookingRaw(bookingRequest, Hooks.token);
        // deserialize to POJO for your assertions
        bookingResponse = response.as(BookingResponse.class);
        logger.debug("POST request sent to create booking");
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

        logger.info("Booking successfully created with ID: {}", bookingResponse.getBookingid());
        logger.info("Check-in/out dates verified: {} → {}",
                bookingResponse.getBookingdates().getCheckin(),
                bookingResponse.getBookingdates().getCheckout());

        // Share booking response with GetBookingSteps
        GetBookingSteps.setBookingResponse(bookingResponse);
    }

    @And("booking response should match the {string} json schema")
    public void booking_response_should_match_the_json_schema(String schemaFileName) {
        // Ensure response object exists (from your positive scenario)
        Assert.assertNotNull(bookingResponse, "Booking response should not be null before schema validation");

        // Use JsonSchemaValidator to check response body
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemes/" + schemaFileName + ".json"));

        logger.info("Booking response successfully validated against schema: {}.json", schemaFileName);
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
        logger.debug("POST request sent with custom booking data");
    }

    @Then("The response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(
                actualStatusCode,
                expectedStatusCode.intValue(),
                "Unexpected status code! Response: " + response.getBody().asString()
        );
        logger.info("Verified status code: {}", expectedStatusCode);
    }

    @Then("The response should contain error {string}")
    public void the_response_should_contain_error(String expectedErrors) {
        List<String> actualErrors = getActualErrors();
        List<String> expectedTrimmed = parseExpectedErrors(expectedErrors);

        logger.debug("Actual errors: {}", actualErrors);
        logger.debug("Expected errors: {}", expectedTrimmed);

        expectedTrimmed.forEach(expected ->
                assertErrorPresent(actualErrors, expected)
        );
    }

    private List<String> getActualErrors() {
        List<String> errors = response.jsonPath().getList("errors", String.class);
        Assert.assertNotNull(errors, "'errors' field is missing in response!");
        return errors;
    }

    private List<String> parseExpectedErrors(String expectedErrors) {
        return Arrays.stream(expectedErrors.split(","))
                .map(String::trim)
                .toList();
    }

    private void assertErrorPresent(List<String> actualErrors, String expected) {
        boolean matchFound = actualErrors.stream()
                .anyMatch(actual -> actual.toLowerCase().contains(expected.toLowerCase()));

        Assert.assertTrue(
                matchFound,
                String.format("Expected error not found! Missing: %s%nActual errors: %s", expected, actualErrors)
        );

        logger.info("Verified error: {}", expected);
    }

    private Object parseRoomId(String value) {
        if (isNullOrBlank(value)) return null;

        String trimmed = value.trim();
        TypeHandler handler = detectTypeHandler(trimmed);
        return handler.parse(trimmed);
    }

    private TypeHandler detectTypeHandler(String value) {
        return HANDLERS.stream()
                .filter(h -> h.matches(value))
                .findFirst()
                .orElse(TypeHandler.STRING);
    }

    // ---- Helper structure ----

    private static final List<TypeHandler> HANDLERS = List.of(
            new TypeHandler("BOOLEAN", v -> v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false"), Boolean::parseBoolean),
            new TypeHandler("INTEGER", CreateBookingSteps::isParsableInt, Integer::parseInt),
            new TypeHandler("DOUBLE", CreateBookingSteps::isParsableDouble, Double::parseDouble),
            TypeHandler.STRING // fallback
    );

    private static boolean isParsableInt(String v) {
        try {
            Integer.parseInt(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isParsableDouble(String v) {
        try {
            Double.parseDouble(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ---- Inner class ----

    private static class TypeHandler {
        final String name;
        final Predicate<String> matcher;
        final Function<String, Object> parser;

        static final TypeHandler STRING = new TypeHandler("STRING", v -> true, v -> v);

        TypeHandler(String name, Predicate<String> matcher, Function<String, Object> parser) {
            this.name = name;
            this.matcher = matcher;
            this.parser = parser;
        }

        boolean matches(String value) {
            return matcher.test(value);
        }

        Object parse(String value) {
            return parser.apply(value);
        }
    }

    private Object parseDeposit(String value) {
        if (isNullOrBlank(value)) return null;
        return parseValueWithFallback(value);
    }

    private Object parseValueWithFallback(String value) {
        return tryParseBoolean(value)
                .orElseGet(() -> tryParseInteger(value)
                        .orElse(value));
    }

    private Optional<Object> tryParseBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)
                ? Optional.of(Boolean.parseBoolean(value))
                : Optional.empty();
    }

    private Optional<Object> tryParseInteger(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Object parseJsonValue(String value) {
        if (isNullOrBlank(value)) return null;

        value = value.trim();
        return parseJsonIfPossible(value);
    }

    private Object parseJsonIfPossible(String value) {
        return isJsonLike(value) ? tryParseJson(value).orElse(value) : value;
    }

    private Optional<Object> tryParseJson(String value) {
        try {
            return Optional.of(objectMapper.readValue(value, Object.class));
        } catch (IOException e) {
            logger.debug("Failed to parse JSON value: {}, error: {}", value, e.getMessage());
            return Optional.empty();
        }
    }

    private boolean isJsonLike(String value) {
        return !isNullOrBlank(value) &&
                (startsWithJsonSymbol(value) || isBooleanOrNumber(value));
    }

    private boolean startsWithJsonSymbol(String value) {
        return StringUtils.startsWithAny(value, "{", "[");
    }

    private boolean isBooleanOrNumber(String value) {
        return value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("false")
                || value.matches("-?\\d+(\\.\\d+)?");
    }

    // Utility to check for null, empty, or literal "null"
    private boolean isNullOrBlank(String value) {
        return StringUtils.isBlank(value) || "null".equalsIgnoreCase(value);
    }

    @When("user sends PUT request to update booking ID {int}")
    public void user_sends_PUT_request_to_update_booking_ID(Integer id) {
        bookingApi = new BookingApi();
        response = bookingApi.updateBooking(id, bookingRequest, Hooks.token);
        logger.debug("PUT request sent to update booking ID: {}", id);
    }

    @When("user sends PUT request to update booking ID {int} with token {string}")
    public void user_sends_PUT_request_to_update_booking_ID_with_token(Integer id, String token) {
        bookingApi = new BookingApi();
        response = bookingApi.updateBooking(id, bookingRequest, token);
        logger.debug("PUT request sent to update booking ID: {} with custom token", id);
    }

    @When("user sends PATCH request to partially update booking ID {int}")
    public void user_sends_PATCH_request_to_partially_update_booking_ID(Integer id) {
        bookingApi = new BookingApi();
        response = bookingApi.partialUpdateBooking(id, bookingRequest, Hooks.token);
        logger.debug("PATCH request sent to partially update booking ID: {}", id);
    }

    @When("user sends PATCH request to partially update booking ID {int} with token {string}")
    public void user_sends_PATCH_request_to_partially_update_booking_ID_with_token(Integer id, String token) {
        bookingApi = new BookingApi();
        response = bookingApi.partialUpdateBooking(id, bookingRequest, token);
        logger.debug("PATCH request sent to partially update booking ID: {} with custom token", id);
    }

    @When("user send a PUT request to update booking ID {int} with")
    public void user_send_a_PUT_request_to_update_booking_ID_with(Integer id, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        bookingRequest = new BookingRequest();
        bookingRequest.setRoomidRaw(parseRoomId(data.get("roomid")));
        bookingRequest.setFirstnameRaw(parseJsonValue(data.get("firstname")));
        bookingRequest.setLastnameRaw(parseJsonValue(data.get("lastname")));
        bookingRequest.setDepositpaidRaw(parseDeposit(data.get("depositpaid")));

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin(data.get("checkin"));
        bookingDates.setCheckout(data.get("checkout"));
        bookingRequest.setBookingdates(bookingDates);
        bookingRequest.setEmail(data.get("email"));
        bookingRequest.setPhone(data.get("phone"));

        bookingApi = new BookingApi();
        response = bookingApi.updateBookingRaw(id, bookingRequest, Hooks.token);
        logger.debug("PUT request sent to update booking ID: {} with custom data", id);
    }
}

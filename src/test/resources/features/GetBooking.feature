Feature: Get Booking by ID

  @Positive @knownIssue
    #Inconsistent since open api resets
  Scenario: Retrieve booking details for a valid ID
    Given user have valid authentication token
    Given user has a booking ID 4
    When user sends GET request for booking
    Then response status code should be 200
    And booking details should be returned successfully

  @Positive @knownIssue
    #Need to update roomid in bookingData.json before running this test
  Scenario: Create a booking and retrieve it using the same ID
    Given user have valid authentication token
    Given user have valid booking data
    When user sends POST request to create a booking
    Then user should get valid booking response with status code 201
    When user sends GET request for same booking ID
    Then response status code should be 200
    And booking details should be returned successfully

  @Negative @knownIssue
    #Getting 403 error instead 401
  Scenario: Retrieve booking details with invalid token
    Given user has a booking ID 101
    When user sends GET request for booking with token "invalid_token_123"
    Then response status code should be 401
    And response should contain error message "Unauthorized"

  @Negative
  Scenario: Retrieve booking details for non-existing ID
    Given user have valid authentication token
    Given user has a booking ID 99999
    When user sends GET request for booking
    Then response status code should be 404
    And response should contain error message "Failed to fetch booking: 404"
    
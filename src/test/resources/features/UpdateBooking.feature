Feature: Update Booking API

  Background:
    Given user have valid authentication token

  @Positive @knownIssue
    #Getting 401 Unauthorized instead of 200
  Scenario: Successfully update an existing booking using PUT
    Given user have valid booking data
    When user sends PUT request to update booking ID 1
    Then The response status code should be 200
    And user should get valid booking response with status code 200

  @Positive @knownIssue
    #Getting 405 Method Not Allowed instead of 200
  Scenario: Successfully update selected fields of a booking using PATCH
    Given user have valid booking data
    When user sends PATCH request to partially update booking ID 1
    Then The response status code should be 200
    And user should get valid booking response with status code 200

  @Negative @knownIssue
    #Getting 401 Unauthorized with error message blank[]
  Scenario: Update booking using PUT with invalid token
    Given user have valid booking data
    When user sends PUT request to update booking ID 1 with token "invalidToken123"
    Then The response status code should be 401
    And The response should contain error "Authentication required"

  @Negative @knownIssue
    #Getting 405 Method Not Allowed instead of 401
  Scenario: Partially update booking using PATCH with invalid token
    Given user have valid booking data
    When user sends PATCH request to partially update booking ID 1 with token "invalidToken123"
    Then The response status code should be 401
    And The response should contain error "Authentication required"

  @Negative @knownIssue
    #Getting 401 Unauthorized
  Scenario Outline: Validate booking update with invalid data using PUT
    When user send a PUT request to update booking ID 1 with
      | roomid      | <roomid>      |
      | firstname   | <firstname>   |
      | lastname    | <lastname>    |
      | depositpaid | <depositpaid> |
      | checkin     | <checkin>     |
      | checkout    | <checkout>    |
      | email       | <email>       |
      | phone       | <phone>       |
    Then The response status code should be <expectedStatusCode>
    And The response should contain error "<expectedError>"

    Examples:
      | roomid | firstname | lastname | depositpaid | checkin    | checkout   | email               | phone                  | expectedStatusCode | expectedError                       |
      | -9     | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900            | 400                | must be greater than or equal to 1  |
      | 3      | AB        | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900            | 400                | size must be between 3 and 18       |
      | 3      | ABC       |          | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900            | 400                | Lastname should not be blank        |
      | 3      | ABC       | DEF      | true        |            | 2025-11-02 | abc.def@example.com | 12345678901            | 400                | must not be null                    |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | null       | abc.def@example.com | 12345678901            | 400                | must not be null                    |
      | 77     | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 1234567890123456789000 | 400                | size must be between 11 and 21      |
      | 77     | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.defexample.com  | 12345678901            | 400                | must be a well-formed email address |

@Negative
Feature: Create Booking

  Scenario Outline: Validate booking creation with invalid data
    Given user have valid authentication token
    When user send a POST request to create booking with
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
      | roomid | firstname | lastname | depositpaid | checkin    | checkout   | email               | phone       | expectedStatusCode | expectedError                       |
      | 6      | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900 | 409                | Failed to create booking            |
      | 3      | AB        | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900 | 400                | size must be between 3 and 18       |
      | 3      | ABC       | D        | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900 | 400                | size must be between 3 and 18       |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | 2025-10-30 | abc.def@example.com | 12345678900 | 400                | Failed to create booking            |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.defexample.com  | 12345678900 | 400                | must be a well-formed email address |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 1234        | 400                | size must be between 11 and 21      |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900 | 403                | Unauthorized access                 |
      | 3      | ABC       | DEF      | true        | 2025-11-01 | 2025-11-02 | abc.def@example.com | 12345678900 | 409                | Duplicate booking for same dates    |
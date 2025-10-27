Feature: Create Booking

  Background:
    Given user have valid authentication token

  @Positive
  Scenario: Successfully create a booking
    Given user have valid booking data
    When user sends POST request to create a booking
    Then user should get valid booking response with status code 201
    And booking response should match the "CreateBooking" json schema

  @Negative
  Scenario Outline: Validate booking creation with invalid data
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
      | roomid | firstname            | lastname                        | depositpaid | checkin        | checkout              | email                  | phone                  | expectedStatusCode | expectedError                       |
      | -9     | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | must be greater than or equal to 1  |
      | "21A"  | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | null   | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | must be greater than or equal to 1  |
      | true   | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      |        | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | must be greater than or equal to 1  |
      | 40     | ABC                  | DEF                             | "21"        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | 3      | AB                   | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | size must be between 3 and 18       |
      | 3      | ABCDEFGHIJKLMNOPQRST | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | size must be between 3 and 18       |
      | 3      |                      | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Firstname should not be blank       |
      | 3      | {"value":"John"}     | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | 3      | ["John"]             | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | 45     | null                 | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Firstname should not be blank       |
      | 3      | ABC                  | DE                              | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | size must be between 3 and 30       |
      | 3      | ABC                  | ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | size must be between 3 and 30       |
      | 3      | ABC                  |                                 | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Lastname should not be blank        |
      | 3      | ABC                  | {"value":"Deo"}                 | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | 3      | ABC                  | ["Deo"]                         | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Failed to create booking            |
      | 45     | ABC                  | null                            | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 12345678900            | 400                | Lastname should not be blank        |
      | 77     | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 1234567890             | 400                | size must be between 11 and 21      |
      | 77     | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.def@example.com    | 1234567890123456789000 | 400                | size must be between 11 and 21      |
      | 77     | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | abc.defexample.com     | 12345678901            | 400                | must be a well-formed email address |
      | 77     | ABC                  | DEF                             | true        | 2025-11-01     | 2025-11-02            | ab$c.def@ @example.com | 12345678901            | 400                | must be a well-formed email address |
      | 81     | ABC                  | DEF                             | true        | 2025-11-01     |                       | abc.def@example.com    | 12345678901            | 400                | must not be null                    |
      | 81     | ABC                  | DEF                             | true        |                | 2025-11-02            | abc.def@example.com    | 12345678901            | 400                | must not be null                    |
      | 6001   | ABC                  | DEF                             | true        | 2025-30-11     | 2025-12-02            | abc.def@example.com    | 12345678901            | 400                | Failed to create booking            |
      | 6001   | ABC                  | DEF                             | true        | 2025-12-01     | null                  | abc.def@example.com    | 12345678901            | 400                | Failed to create booking            |
      | 6001   | ABC                  | DEF                             | true        | 2025-12-01     | JAN                   | abc.def@example.com    | 12345678901            | 400                | Failed to create booking            |
      | 6001   | ABC                  | DEF                             | true        | 2025-12-01     | {"date":"2025-11-02"} | abc.def@example.com    | 12345678901            | 400                | Failed to create booking            |
      | 6001   | ABC                  | DEF                             | true        | ["2025-11-01"] | 2025-12-02            | abc.def@example.com    | 12345678901            | 400                | Failed to create booking            |

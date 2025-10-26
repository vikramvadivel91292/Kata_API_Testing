Feature: Create Booking positive tests

  Scenario: User should be able to create a booking using valid data
    Given user have valid authentication token
    And user have valid booking data
    When user sends POST request to create a booking
    Then user should get valid booking response with status code 201
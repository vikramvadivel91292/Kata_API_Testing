Feature: Delete Booking API

  @Positive @knownIssue
    #Getting 401 error instead of 201
  Scenario: Delete booking successfully with valid token
    Given user wants to delete booking ID 1
    When user sends DELETE request for that booking
    Then the response status code should be 201
    And booking should be deleted successfully

  @Negative
  Scenario: Delete booking with invalid token
    Given user wants to delete booking ID 1
    When user sends DELETE request with invalid token "invalidToken123"
    Then the response status code should be 401
    And response should contain error message "Authentication required"

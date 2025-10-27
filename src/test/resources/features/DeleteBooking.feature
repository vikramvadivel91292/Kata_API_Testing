Feature: Delete Booking API

  @Positive @knownIssue
    #Getting 401 error instead of 201
  Scenario: Delete booking successfully with valid token
    Given user has a valid booking ID 1
    When user sends DELETE request for that booking
    Then delete response status code should be 201
    And booking should be deleted successfully

  @Negative
  Scenario: Delete booking with invalid token
    Given user has a valid booking ID 1
    When user sends DELETE request with invalid token "invalidToken123"
    Then delete response status code should be 401
    And delete response should contain error message "Authentication required"

  @Negative @knownIssue
    #Getting 401 error instead of 400
  Scenario: Delete booking using invalid ID
    Given user has a valid booking ID 0
    When user sends DELETE request for that booking
    Then delete response status code should be 400
    And delete response should contain error message "Invalid booking ID"

  @Negative @knownIssue
    #Getting 401 error instead of 404
  Scenario: Delete booking using non-existent ID
    Given user has a valid booking ID 999999
    When user sends DELETE request for that booking
    Then delete response status code should be 404
    And delete response should contain error message "booking ID not found"

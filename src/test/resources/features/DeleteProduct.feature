Feature: Product DELETE Operations
  As a user of the Product API
  I want to delete products through DELETE endpoints
  So that I can remove products from the system

  Background:
    Given the application is running with a clean database
    And the following products exist:
      | name           | price   | category    | active |
      | Laptop Dell    | 1299.99 | Electronics | true   |
      | Mouse Logitech |   29.99 | Electronics | true   |
      | Keyboard RGB   |  149.99 | Electronics | true   |
      | Monitor 4K     |  399.99 | Electronics | true   |
      | Headphones     |  199.99 | Electronics | false  |

  @delete @success
  Scenario: Delete a product successfully
    Given a product exists with ID 1
    When I send a DELETE request to "/api/v1/products/{id}" using the first product ID
    Then the response status should be 204
    And the product should be removed from the database

  @delete @error-handling
  Scenario: Delete a non-existent product
    When I send a DELETE request to "/api/v1/products/999"
    Then the response status should be 404
    And the response should contain an error message


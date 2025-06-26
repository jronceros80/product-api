Feature: Product PUT Operations
  As a user of the Product API
  I want to update products through PUT endpoints
  So that I can modify existing product information

  Background:
    Given the application is running with a clean database
    And the following products exist:
      | name           | price   | category    | active |
      | Laptop Dell    | 1299.99 | Electronics | true   |
      | Mouse Logitech |   29.99 | Electronics | true   |
      | Keyboard RGB   |  149.99 | Electronics | true   |

  @put @update @success
  Scenario: Update an existing product successfully
    Given a product exists with ID 1
    When I send a PUT request to "/api/v1/products/{id}" using the first product ID with:
      | name     | Updated Laptop Dell |
      | price    |             1399.99 |
      | category | Electronics         |
      | active   | true                |
    Then the response status should be 200
    And the response should contain the updated product
    And the product should be updated in the database

  @put @update @error-handling
  Scenario: Update a non-existent product
    When I send a PUT request to "/api/v1/products/999" with:
      | name     | Non-existent Product |
      | price    |               999.99 |
      | category | Electronics          |
      | active   | true                 |
    Then the response status should be 404
    And the response should contain an error message
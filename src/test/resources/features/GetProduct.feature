Feature: Product GET Operations
  As a user of the Product API
  I want to retrieve products through GET endpoints
  So that I can view product information and lists

  Background:
    Given the application is running with a clean database
    And the following products exist:
      | name           | price   | category    | active |
      | Laptop Dell    | 1299.99 | Electronics | true   |
      | Mouse Logitech |   29.99 | Electronics | true   |
      | Keyboard RGB   |  149.99 | Electronics | true   |
      | Monitor 4K     |  399.99 | Electronics | true   |
      | Headphones     |  199.99 | Electronics | false  |

  @get @product-detail
  Scenario: Get a product by ID successfully
    Given a product exists with ID 1
    When I send a GET request to "/api/v1/products/{id}" using the first product ID
    Then the response status should be 200
    And the response should contain the product details
    And the response should include HATEOAS links

  @get @pagination
  Scenario: Get all active products - first page
    When I send a GET request to "/api/v1/products?page=0&size=2&active=true"
    Then the response status should be 200
    And the response should contain 2 products
    And the response should include pagination metadata
    And the response should include HATEOAS navigation links

  @get @sorting
  Scenario: Get products with sorting
    When I send a GET request to "/api/v1/products?sort=price,desc&active=true"
    Then the response status should be 200
    And the products should be sorted by price in descending order

  @get @error-handling
  Scenario: Get a non-existent product
    When I send a GET request to "/api/v1/products/999"
    Then the response status should be 404
    And the response should contain an error message

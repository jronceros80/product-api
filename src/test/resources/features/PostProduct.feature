Feature: Product POST Operations
  As a user of the Product API
  I want to create products through POST endpoints
  So that I can add new products to the system

  Background:
    Given the application is running with a clean database

  @post @create @success
  Scenario: Create a new product successfully
    When I send a POST request to "/api/v1/products" with:
      | name     | Gaming Chair |
      | price    |       299.99 |
      | category | Electronics  |
      | active   | true         |
    Then the response status should be 201
    And the response should contain the created product
    And the product should be saved in the database

  @post @create @default-active
  Scenario: Create product without specifying active status
    When I send a POST request to "/api/v1/products" with:
      | name     | Default Active Product |
      | price    |                  99.99 |
      | category | Electronics            |
    Then the response status should be 201
    And the response should contain the created product
    And the product should be active by default



package com.products.integration.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private Response response;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .when()
                .get(endpoint);
    }

    @When("I send a GET request to {string} using the first product ID")
    public void iSendAGetRequestToUsingTheFirstProductId(String endpoint) {
        String url = endpoint.replace("{id}", commonSteps.getFirstProductId().toString());
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .when()
                .get(url);
    }

    @When("I send a POST request to {string} with:")
    public void iSendAPostRequestToWith(String endpoint, DataTable dataTable) {
        Map<String, Object> requestBody = convertDataTableToMap(dataTable);
        
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("I send a PUT request to {string} with:")
    public void iSendAPutRequestToWith(String endpoint, DataTable dataTable) {
        Map<String, Object> requestBody = convertDataTableToMap(dataTable);
        
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(endpoint);
    }

    @When("I send a PUT request to {string} using the first product ID with:")
    public void iSendAPutRequestToUsingTheFirstProductIdWith(String endpoint, DataTable dataTable) {
        String url = endpoint.replace("{id}", commonSteps.getFirstProductId().toString());
        Map<String, Object> requestBody = convertDataTableToMap(dataTable);
        
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(url);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .when()
                .delete(endpoint);
    }

    @When("I send a DELETE request to {string} using the first product ID")
    public void iSendADeleteRequestToUsingTheFirstProductId(String endpoint) {
        String url = endpoint.replace("{id}", commonSteps.getFirstProductId().toString());
        response = RestAssured
                .given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .when()
                .delete(url);
    }

    private Map<String, Object> convertDataTableToMap(DataTable dataTable) {
        Map<String, Object> map = new HashMap<>();
        List<List<String>> rows = dataTable.asLists(String.class);

        for (List<String> row : rows) {
            if (row.size() >= 2) {
                String key = row.get(0);
                String value = row.get(1);

                if ("price".equals(key)) {
                    map.put(key, new BigDecimal(value));
                } else if ("active".equals(key)) {
                    map.put(key, Boolean.parseBoolean(value));
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    public Response getResponse() {
        return response;
    }
} 
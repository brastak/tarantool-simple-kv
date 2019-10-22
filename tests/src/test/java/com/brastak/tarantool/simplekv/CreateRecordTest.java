package com.brastak.tarantool.simplekv;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateRecordTest extends AbstractKVTest {
    @BeforeAll
    static void setUp() {
        // Remove all records can be created during test - handle case when test fails during execution
        given().baseUri(getBaseServiceUri()).delete("create-valid");
        given().baseUri(getBaseServiceUri()).delete("duplicate");
    }

    @Test
    void testInvalidUrl() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "invalid");
        payload.put("value", "invalid");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post("extra-path")
        .then()
                .statusCode(400);
    }

    @Test
    void testEmptyPayload() {
        given()
                .spec(spec)
        .when()
                .post()
        .then()
                .statusCode(400);
    }


    @Test
    void testInvalidJson() {
        given()
                .spec(spec)
                .body("Invalid JSON")
        .when()
                .post()
        .then()
                .statusCode(400);
    }


    @Test
    void testMissedKey() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("missed-key", "invalid");
        payload.put("value", "invalid");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(400);
    }

    @Test
    void testKeyIsNull() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", null);
        payload.put("value", "invalid");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(400);
    }

    @Test
    void testKeyIsNotString() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", 123);
        payload.put("value", "invalid");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(400);
    }

    @Test
    void testMissedValue() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "invalid");
        payload.put("missed-value", "invalid");

        given()
                .spec(spec)
                .body(payload)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testValueIsNull() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "invalid");
        payload.put("value", null);

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(400);
    }

    @Test
    void testSuccessful() {
        Data data = new Data(
                "foo", 123, new Data (
                        "bar", 321, null
                )
        );

        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "create-valid");
        payload.put("value", data);

        // Test post returns value
        Data result = given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(201)
                .extract().body().as(Data.class);

        assertEquals(data, result, "Unexpected response");

        // Test retrieve value by key
        Data retrieved = given()
                .spec(spec)
        .when()
                .get("create-valid")
        .then()
                .statusCode(200)
                .extract().body().as(Data.class);

        assertEquals(data, retrieved, "Unexpected response");
    }

    @Test
    void testDuplicatedKeys() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "duplicate");
        payload.put("value", "duplicate");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(201);

        given()
                .spec(spec)
                .body(payload)
        .when()
                .post()
        .then()
                .statusCode(409);

        given().spec(spec).when().delete("duplicate").then().statusCode(204);
    }

    @AfterAll
    static void tearDown() {
        given().baseUri(getBaseServiceUri()).delete("create-valid");
    }
}

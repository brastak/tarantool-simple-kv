package com.brastak.tarantool.simplekv;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateRecordTest extends AbstractKVTest {
    private static Data oldData;
    @BeforeAll
    static void setUp() {
        // Remove all records can be created during test - handle case when test fails during execution
        given().baseUri(getBaseServiceUri()).delete("update-valid");

        oldData = new Data(
                "foo", 123, new Data (
                    "bar", 321, null
                )
        );

        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "update-valid");
        payload.put("value", oldData);

        given().baseUri(getBaseServiceUri()).body(payload).post();
    }

    @Test
    void testMissedKey() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("data", "missed-key");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .put()
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidUri() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("data", "invalid-uri");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .put("update-valid/extra-path")
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidKey() {
        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("data", "invalid-key");

        given()
                .spec(spec)
                .body(payload)
        .when()
                .put("invalid-key")
        .then()
                .statusCode(404);
    }

    @Test
    void testEmptyPayload() {
        given()
                .spec(spec)
        .when()
                .put("update-valid")
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidJson() {
        given()
                .spec(spec)
                .body("Invalid JSON")
        .when()
                .put("update-valid")
        .then()
                .statusCode(400);
    }

    @Test
    void testSuccessful() {
        Data retrievedBefore = given()
                .spec(spec)
        .when()
                .get("update-valid").as(Data.class);
        assertEquals(oldData, retrievedBefore, "Unexpected data");

        Data newData = new Data("baz", 111, null);
        // Test put returns value
        Data result = given()
                .spec(spec)
                .body(newData)
        .when()
                .put("update-valid")
        .then()
                .statusCode(200)
                .extract().body().as(Data.class);
        assertEquals(newData, result, "Unexpected data");

        // Test retrieve new value by key
        Data retrievedAfter = given()
                .spec(spec)
        .when()
                .get("update-valid")
        .then()
                .statusCode(200)
                .extract().body().as(Data.class);
        assertEquals(newData, retrievedAfter, "Unexpected data");
    }

    @AfterAll
    static void tearDown() {
        given().baseUri(getBaseServiceUri()).delete("update-valid");
    }
}

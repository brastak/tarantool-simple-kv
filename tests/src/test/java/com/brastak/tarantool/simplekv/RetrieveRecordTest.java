package com.brastak.tarantool.simplekv;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RetrieveRecordTest extends AbstractKVTest {
    private static Data data;

    @BeforeAll
    static void setUp() {
        // Remove all records can be created during test - handle case when test fails during execution
        given().baseUri(getBaseServiceUri()).delete("retrieve-valid");

        data = new Data(
                "foo", 123, new Data (
                    "bar", 321, null
                )
        );

        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "retrieve-valid");
        payload.put("value", data);

        given().baseUri(getBaseServiceUri()).body(payload).post();
    }

    @Test
    void testMissedKey() {
        given()
                .spec(spec)
        .when()
                .get()
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidUri() {
        given()
                .spec(spec)
        .when()
                .get("retrieve-valid/extra-path")
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidKey() {
        given()
                .spec(spec)
        .when()
                .get("invalid-key")
        .then()
                .statusCode(404);
    }

    @Test
    void testSuccessful() {
        Data result = given()
                .spec(spec)
        .when()
                .get("retrieve-valid")
        .then()
                .statusCode(200)
                .extract().body().as(Data.class);

        assertEquals(data, result, "Unexpected response");
    }

    @AfterAll
    static void tearDown() {
        given().baseUri(getBaseServiceUri()).delete("retrieve-valid");
    }
}

package com.brastak.tarantool.simplekv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

class DeleteRecordTest extends AbstractKVTest {
    @BeforeAll
    static void setUp() {
        // Remove all records can be created during test - handle case when test fails during execution
        given().baseUri(getBaseServiceUri()).delete("delete-valid");

        Data data = new Data(
                "foo", 123, new Data (
                    "bar", 321, null
                )
        );

        Map<String, ? super Object> payload = new HashMap<>();
        payload.put("key", "delete-valid");
        payload.put("value", data);

        given().baseUri(getBaseServiceUri()).body(payload).post();
    }

    @Test
    void testMissedKey() {
        given()
                .spec(spec)
        .when()
                .delete()
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidUri() {
        given()
                .spec(spec)
        .when()
                .delete("delete-valid/extra-path")
        .then()
                .statusCode(400);
    }

    @Test
    void testInvalidKey() {
        given()
                .spec(spec)
        .when()
                .delete("invalid-key")
        .then()
                .statusCode(404);
    }

    @Test
    void testSuccessful() {
        given()
                .spec(spec)
        .when()
                .delete("delete-valid")
        .then()
                .statusCode(204);

        given()
                .spec(spec)
        .when()
                .delete("delete-valid")
        .then()
                .statusCode(404);
    }
}

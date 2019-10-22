package com.brastak.tarantool.simplekv;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

abstract class AbstractKVTest {
    final RequestSpecification spec;

    AbstractKVTest() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setAccept(ContentType.JSON);
        builder.setBaseUri(getBaseServiceUri());
        this.spec = builder.build();
    }

    static String getBaseServiceUri() {
        return System.getProperty("kv.uri.base", "http://ec2-18-185-24-222.eu-central-1.compute.amazonaws.com/kv");
    }
}

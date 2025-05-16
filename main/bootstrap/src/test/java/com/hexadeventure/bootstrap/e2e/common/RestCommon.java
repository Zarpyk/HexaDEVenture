package com.hexadeventure.bootstrap.e2e.common;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RestCommon {
    public static Response get(int port, String path) {
        return get(port, path, true);
    }
    
    public static Response get(int port, String path, boolean withAuth) {
        if(withAuth) {
            return given().port(port).and()
                          .auth().preemptive().basic(UserFactory.EMAIL, UserFactory.PASSWORD)
                          .when().get(path);
        } else {
            return given().port(port)
                          .when().get(path);
        }
    }
    
    public static Response post(int port, String path) {
        return post(port, path, true);
    }
    
    public static Response post(int port, String path, boolean withAuth) {
        if(withAuth) {
            return given().port(port).and()
                          .auth().preemptive().basic(UserFactory.EMAIL, UserFactory.PASSWORD)
                          .when().post(path);
        } else {
            return given().port(port)
                          .when().post(path);
        }
    }
    
    public static Response postWithBody(int port, String path, Object body) {
        return postWithBody(port, path, body, true);
    }
    
    public static Response postWithBody(int port, String path, Object body, boolean withAuth) {
        if(withAuth) {
            return given().port(port).and()
                          .auth().preemptive().basic(UserFactory.EMAIL, UserFactory.PASSWORD)
                          .contentType(ContentType.JSON).body(body)
                          .when().post(path);
        } else {
            return given().port(port).and()
                          .contentType(ContentType.JSON).body(body)
                          .when().post(path);
        }
    }
}

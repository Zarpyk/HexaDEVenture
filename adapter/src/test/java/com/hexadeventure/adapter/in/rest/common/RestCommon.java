package com.hexadeventure.adapter.in.rest.common;

import com.hexadeventure.adapter.common.ApplicationExceptionHandlers;
import com.hexadeventure.adapter.common.GenericExceptionHandler;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.principal;

public class RestCommon {
    public static void Setup(Object... controllers) {
        RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        StandaloneMockMvcBuilder mvcBuilders = MockMvcBuilders.standaloneSetup(controllers)
                                                              .setControllerAdvice(new GenericExceptionHandler(),
                                                                                   new ApplicationExceptionHandlers());
        RestAssuredMockMvc.standaloneSetup(mvcBuilders);
    }
    
    public static MockMvcResponse get(String path) {
        return get(path, true);
    }
    
    public static MockMvcResponse get(String path, boolean withAuth) {
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        return given().when().get(path);
    }
    
    public static MockMvcResponse getWithParam(String path, String... params) {
        return getWithParam(path, true, params);
    }
    
    public static MockMvcResponse getWithParam(String path, boolean withAuth, String... params) {
        if(params.length % 2 != 0) throw new IllegalArgumentException("Params must be in key-value pairs");
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        
        Map<String, String> paramMap = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            paramMap.put(params[i], params[i + 1]);
        }
        
        return given().params(paramMap).when().get(path);
    }
    
    public static MockMvcResponse post(String path) {
        return post(path, true);
    }
    
    public static MockMvcResponse post(String path, boolean withAuth) {
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        return given().when().post(path);
    }
    
    public static MockMvcResponse postWithBody(String path, Object body) {
        return postWithBody(path, body, true);
    }
    
    public static MockMvcResponse postWithBody(String path, Object body, boolean withAuth) {
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        return given().contentType(ContentType.JSON).body(body)
                      .when().post(path);
    }
    
    public static MockMvcResponse postWithParam(String path, String... params) {
        return postWithParam(path, true, params);
    }
    
    public static MockMvcResponse postWithParam(String path, boolean withAuth, String... params) {
        if(params.length % 2 != 0) throw new IllegalArgumentException("Params must be in key-value pairs");
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        
        Map<String, String> paramMap = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            paramMap.put(params[i], params[i + 1]);
        }
        
        return given().params(paramMap).when().post(path);
    }
    
    public static MockMvcResponse delete(String path) {
        return delete(path, true);
    }
    
    public static MockMvcResponse delete(String path, boolean withAuth) {
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        return given().when().delete(path);
    }
    
    public static MockMvcResponse deleteWithBody(String path, Object body) {
        return deleteWithBody(path, body, true);
    }
    
    public static MockMvcResponse deleteWithBody(String path, Object body, boolean withAuth) {
        if(withAuth) RestAssuredMockMvc.authentication = principal(new UserPrincipal());
        else RestAssuredMockMvc.authentication = null;
        return given().contentType(ContentType.JSON).body(body)
                      .when().delete(path);
    }
}

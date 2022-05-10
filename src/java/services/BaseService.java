package services;

import dados.Body;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseService {


    public Response doPostRequest(Body body, String resource) {
        return  given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(resource);
    }

    public Response doGetRequest(String resource) {
        return  given()
                .when()
                .get(resource);
    }

    public Response doPutRequest(Body body, String resource) {
        return given()
                .contentType("application/json")
                .body(body)
                .when()
                .put(resource);
    }

    public Response doDeleteRequest(String resource) {
        return given()
                .contentType("application/json")
                .when()
                .delete(resource);
    }
}
